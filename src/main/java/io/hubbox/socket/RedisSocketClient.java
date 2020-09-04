package io.hubbox.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ServerConnectListener;
import io.hubbox.listener.ServerDisconnectListener;
import io.hubbox.listener.ServerListener;
import io.hubbox.manager.ClientConnectionManager;
import io.hubbox.manager.ClientInfo;
import io.lettuce.core.RedisClient;

public class RedisSocketClient extends SocketBase implements ServerListener {

    private ClientConnectionManager connectionManager;
    private RedisIOClient redisIOClient;

    public RedisSocketClient(RedisClient redisClient) {
        super(redisClient);
        redisIOClient = new RedisIOClient();
        connectionManager = new ClientConnectionManager(redisClient, redisIOClient, publisherCommand);
        System.out.println("RedisSocketClient constructor");
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
    public void start() {
        new Thread(() -> {
            while (true) {
                commands.hset(redisIOClient.getClientData().getSessionId(), ClientInfo.STATUS.getValue(), ClientInfo.OK.getValue());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
