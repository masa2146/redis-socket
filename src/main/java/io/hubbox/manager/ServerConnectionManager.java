package io.hubbox.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ClientConnectListener;
import io.hubbox.socket.RedisSocketServer;
import io.hubbox.socket.SocketBase;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.io.IOException;

public class ServerConnectionManager {

    private RedisClient redisClient;
    private RedisCommands<String, String> commands;
    RedisPubSubCommands<String, String> publisherCommand;
    private StatefulRedisPubSubConnection<String, String> subConnection;

    private ObjectMapper objectMapper;

    public ServerConnectionManager(RedisClient redisClient, RedisCommands<String, String> commands,RedisPubSubCommands<String, String> publisherCommand) {
        this.redisClient = redisClient;
        this.commands = commands;
        subConnection = redisClient.connectPubSub();
        objectMapper = new ObjectMapper();
    }

    public void listenConnectedClients(ClientConnectListener connectListener) {
        subConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                try {
                    RedisIOClient redisIOClient = objectMapper.readValue(message, RedisIOClient.class);
                    connectListener.onConnect(redisIOClient);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void message(String s, String k1, String s2) {

            }

            @Override
            public void subscribed(String s, long l) {

            }

            @Override
            public void psubscribed(String s, long l) {

            }

            @Override
            public void unsubscribed(String s, long l) {

            }

            @Override
            public void punsubscribed(String s, long l) {

            }
        });

        subConnection.sync().subscribe(SocketBase.EVENT_CONNECTED);
        threadClientStatus();
    }

    private void threadClientStatus(){
        new Thread(() -> {
            while (true) {
                 statusControl();
//                System.out.println("KEYS: " + commands.keys("*"));
//                publisherCommand.publish("marmara","sadasda");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    private synchronized void statusControl() {
        for (String key : commands.keys("*")) {
            if (commands.get(key) != null) {
                if (commands.get(key).equals("0")) {
                    System.out.println("CLIENT DISCONNECTED. CLIENT ID: " + key);
                }
            }
            publisherCommand.publish(SocketBase.EVENT_STATUS + "/" + key, "0");
            commands.set(key, "0");
        }
    }

}
