package io.blt.socket.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blt.exceptions.SendMessageException;
import io.blt.listener.EventListener;
import io.blt.listener.MessageListener;
import io.blt.manager.MessageManager;
import io.blt.manager.cluster.ClientClusterListenerManager;
import io.blt.socket.RedisServerSocket;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.sync.RedisClusterPubSubCommands;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fatih
 */
public abstract class ClusterBaseSocket implements MessageListener {
    private Map<String, ClientClusterListenerManager> managerMap;
    RedisClusterClient redisClient;
    RedisClusterPubSubCommands<String, String> publisherCommand;
    RedisClusterCommands<String, String> commands;

    ClusterBaseSocket(RedisClusterClient redisClient) {
        try {
            this.redisClient = redisClient;
            this.managerMap = new HashMap<>();
            this.commands = redisClient.connect().sync();
            this.publisherCommand = redisClient.connectPubSub().sync();
        } catch (RedisConnectionException e) {
            // throws exception
        }
    }

    @Override
    public void addEventListener(EventListener eventListener, String... channels) {
        try {
            MessageManager messageManager = new MessageManager(eventListener);
            StatefulRedisClusterPubSubConnection<String, String> pubSubListener = redisClient.connectPubSub();

            pubSubListener.addListener(messageManager);
            pubSubListener.sync().subscribe(channels);
            ClientClusterListenerManager clientListenerManager = new ClientClusterListenerManager(messageManager, pubSubListener);
            for (String channel : channels) {
                managerMap.put(channel, clientListenerManager);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            // throw exception
            // You can't call sendMessage function without call init. Before call init function
        }
    }

    @Override
    public void removeEventListener(String channel) {
        if (managerMap.get(channel) != null) {
            managerMap.get(channel).removeEvent(channel);
            managerMap.remove(channel);
        }

    }

    @Override
    public <T> void sendMessage(String channel, Class<T> message) throws SendMessageException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            publisherCommand.publish(channel, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new SendMessageException("Your message only contains fields like that String, Integer, Float etc..." +
                    "\nMaybe your message class contain incorrect fields.");
            // throw exception
            // The class must contain only data must not specific function.
        } catch (NullPointerException e) {
            throw new SendMessageException("Make sure RedisSocketClient or RedisSocketServer not NULL or maybe your message is NULL");
            // throw exception
            // You can't call sendMessage function without call init. Before call init function
        }
    }

    @Override
    public void sendMessage(String channel, String message) {
        publisherCommand.publish(channel, message);

    }

    public abstract void start();
}
