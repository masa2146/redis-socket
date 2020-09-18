package io.hubbox.socket.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.EventListener;
import io.hubbox.listener.ServerConnectListener;
import io.hubbox.listener.ServerDisconnectListener;
import io.hubbox.listener.ServerListener;
import io.hubbox.manager.ClientInfo;
import io.hubbox.manager.cluster.ClientClusterConnectionManager;
import io.lettuce.core.cluster.RedisClusterClient;
import lombok.Getter;

/**
 * @author fatih
 */
public class RedisClusterSocketClient extends SocketClusterBase implements ServerListener {

    private ClientClusterConnectionManager connectionManager;
    @Getter
    private RedisIOClient redisIOClient;
    private ObjectMapper mapper;

    public RedisClusterSocketClient(RedisClusterClient redisClient) {
        super(redisClient);
        redisIOClient = new RedisIOClient(redisClient);
        mapper = new ObjectMapper();
        connectionManager = new ClientClusterConnectionManager(redisClient, redisIOClient, publisherCommand);
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

    public void addSelfMessageListener(EventListener eventListener) {
        redisIOClient.addMessageListener(eventListener);
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
