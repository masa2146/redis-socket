package io.hubbox.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hubbox.listener.EventListener;
import io.hubbox.listener.MessageListener;
import io.hubbox.manager.ClientListenerManager;
import io.hubbox.manager.MessageManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.util.HashMap;
import java.util.Map;

public abstract class SocketBase implements MessageListener {


    private Map<String, ClientListenerManager> managerMap = new HashMap<>();
    RedisClient redisClient;
    StatefulRedisPubSubConnection<String, String> pubConnection;
    RedisPubSubCommands<String, String> publisherCommand;
    RedisCommands<String, String> commands;

    SocketBase(RedisClient redisClient) {
        try{
            this.redisClient = redisClient;
            pubConnection = redisClient.connectPubSub();
            commands = redisClient.connect().sync();
            publisherCommand = pubConnection.sync();
        }catch (RedisConnectionException e){
            // throws exception
        }
    }

    @Override
    public void addEventListener(String channel, EventListener eventListener) {
        try {
            MessageManager messageManager = new MessageManager(eventListener);
            StatefulRedisPubSubConnection<String, String> pubSubListener = redisClient.connectPubSub();

            pubSubListener.addListener(messageManager);
            pubSubListener.sync().subscribe(channel);

            managerMap.put(channel, new ClientListenerManager(messageManager, pubSubListener));

        } catch (NullPointerException e) {
            e.printStackTrace();
            // throw exception
            // You can't call sendMessage function without call init. Before call init function
        }
    }

    @Override
    public void removeEventListener(String channel) {
        managerMap.get(channel).removeEvent(channel);
        managerMap.remove(channel);
    }

    @Override
    public <T> void sendMessage(String channel, Class<T> message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            publisherCommand.publish(channel, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // throw exception
            // The class must contain only data must not specific function.
        } catch (NullPointerException e) {
            e.printStackTrace();
            // throw exception
            // You can't call sendMessage function without call init. Before call init function
        }
    }
    @Override
    public void sendMessage(String channel, String message) {
        try {
            publisherCommand.publish(channel, message);
        } catch (NullPointerException e) {
            e.printStackTrace();
            // throw exception
            // You can't call sendMessage function without call init. Before call init function
        }
    }



    public abstract void start();
}
