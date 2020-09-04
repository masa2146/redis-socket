package io.hubbox.socket;

import io.hubbox.listener.ClientConnectListener;
import io.hubbox.listener.ClientListener;
import io.hubbox.manager.ServerConnectionManager;
import io.lettuce.core.RedisClient;

public class RedisSocketServer extends SocketBase implements ClientListener {


    private ServerConnectionManager connectionManager;

    RedisSocketServer(RedisClient redisClient) {
        super(redisClient);
        connectionManager = new ServerConnectionManager(redisClient, commands, publisherCommand);
    }

    @Override
    public void addConnectListener(ClientConnectListener connectListener) {
        connectionManager.listenConnectedClients(connectListener);
    }

    @Override
    public void start() {
        new Thread(() -> {
            while (true) {
            }
        }).run();
    }
}
