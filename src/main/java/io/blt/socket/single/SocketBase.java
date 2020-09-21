package io.blt.socket.single;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blt.exceptions.SendMessageException;
import io.blt.listener.EventListener;
import io.blt.listener.MessageListener;
import io.blt.manager.single.ClientListenerManager;
import io.blt.manager.MessageManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class SocketBase implements MessageListener {


    private Map<String, ClientListenerManager> managerMap;
    RedisClient redisClient;
    RedisPubSubCommands<String, String> publisherCommand;
    @Getter
    RedisCommands<String, String> commands;

    SocketBase(RedisClient redisClient) {
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
            StatefulRedisPubSubConnection<String, String> pubSubListener = redisClient.connectPubSub();

            pubSubListener.addListener(messageManager);
            pubSubListener.sync().subscribe(channels);
            ClientListenerManager clientListenerManager = new ClientListenerManager(messageManager, pubSubListener);
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
