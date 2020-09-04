package io.hubbox.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ClientConnectListener;
import io.hubbox.listener.ClientDisconnectListener;
import io.hubbox.socket.SocketInfo;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.io.IOException;

public class ServerConnectionManager {

    private RedisCommands<String, String> commands;
    private StatefulRedisPubSubConnection<String, String> subConnection;
    private ClientDisconnectListener disconnectListener;

    private ObjectMapper objectMapper;

    public ServerConnectionManager(RedisClient redisClient, RedisCommands<String, String> commands) {
        this.commands = commands;
        subConnection = redisClient.connectPubSub();
        objectMapper = new ObjectMapper();
    }

    /**
     * When client connected to server, server send message to SocketInfo.EVENT_CONNECTED channel.
     * The message contains {@link io.hubbox.client.ClientData}. And it triggers to ClientConnectListener.onConnect() function.
     * Also this function client which  has been connected, add to hash set which is SocketInfo.ALL_CLIENT key
     */
    public void listenConnectedClients(ClientConnectListener connectListener) {
        subConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                try {
                    RedisIOClient redisIOClient = objectMapper.readValue(message, RedisIOClient.class);
                    connectListener.onConnect(redisIOClient);
                    commands.hset(SocketInfo.ALL_CLIENT.getValue(), redisIOClient.getClientData().getSessionId(), message);
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

        subConnection.sync().subscribe(SocketInfo.EVENT_CONNECTED.getValue());
        threadClientStatus();
    }

    public void listenDisconnectClients(ClientDisconnectListener disconnectListener) {
        this.disconnectListener = disconnectListener;
    }

    /**
     * Check clients status ever 1 seconds.
     */
    private void threadClientStatus() {
        new Thread(() -> {
            while (true) {
                try {
                    statusControl();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Connected clients id keep with key in hash map of the Redis. Get all keys and check status field  value of the key.
     * If the status filed is zero(ClientInfo.NOT_OK) then this client is disconnected.
     * It sets one(ClientInfo.OK) value to status field in the key on client side if client connected.
     */
    private synchronized void statusControl() throws IOException {
        try {
            for (String key : commands.keys("*")) {
                if (commands.hget(key, ClientInfo.STATUS.getValue()).equals(ClientInfo.NOT_OK.getValue())) {
                    RedisIOClient redisIOClient = objectMapper.readValue(commands.hget(key, ClientInfo.INFO.getValue()), RedisIOClient.class);
                    commands.hdel(key, ClientInfo.STATUS.getValue(), ClientInfo.INFO.getValue());
                    commands.del(key);
                    commands.hdel(SocketInfo.ALL_CLIENT.getValue(), redisIOClient.getClientData().getSessionId());
                    disconnectListener.onDisconnect(redisIOClient);
                } else {
                    commands.hset(key, ClientInfo.STATUS.getValue(), ClientInfo.NOT_OK.getValue());
                }
            }
        } catch (RedisException e) {
            System.out.println("Error on control the status connection ");
            e.printStackTrace();
        }
    }

}
