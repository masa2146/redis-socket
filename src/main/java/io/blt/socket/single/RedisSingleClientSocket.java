package io.blt.socket.single;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blt.client.RedisIOClient;
import io.blt.listener.EventListener;
import io.blt.listener.ServerConnectListener;
import io.blt.listener.ServerDisconnectListener;
import io.blt.manager.ClientInfo;
import io.blt.manager.single.ClientConnectionManager;
import io.blt.socket.RedisClientSocket;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

public class RedisSingleClientSocket extends SingleBaseSocket implements RedisClientSocket {

    private ClientConnectionManager connectionManager;
    private RedisIOClient redisIOClient;
    private ObjectMapper mapper;

    public RedisSingleClientSocket(RedisClient redisClient) {
        super(redisClient);
        redisIOClient = new RedisIOClient(redisClient);
        mapper = new ObjectMapper();
        connectionManager = new ClientConnectionManager(redisClient, redisIOClient, publisherCommand);
    }

    @Override
    public void addConnectListener(ServerConnectListener connectListener) {
        try {
            connectionManager.checkConnection(connectListener);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDisconnectListener(ServerDisconnectListener disconnectListener) {
        connectionManager.checkDisconnectedListener(disconnectListener);
    }

    @Override
    public void addSelfMessageListener(EventListener eventListener) {
        redisIOClient.addMessageListener(eventListener);
    }

    @Override
    public RedisIOClient getRedisIOClient() {
        return redisIOClient;
    }

    @Override
    public RedisClusterCommands<String, String> getCommands() {
        return commands;
    }

    @Override
    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    commands.hset(redisIOClient.getClientData().getSessionId(), ClientInfo.STATUS.getValue(), ClientInfo.OK.getValue());
                    commands.hset(redisIOClient.getClientData().getSessionId(), ClientInfo.INFO.getValue(), mapper.writeValueAsString(redisIOClient.getClientData()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
