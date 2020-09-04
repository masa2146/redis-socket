package io.hubbox.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ClientConnectListener;
import io.hubbox.listener.ClientDisconnectListener;
import io.hubbox.listener.ClientListener;
import io.hubbox.manager.ServerConnectionManager;
import io.lettuce.core.RedisClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisSocketServer extends SocketBase implements ClientListener {


    private ServerConnectionManager connectionManager;
    private List<RedisIOClient> allClients;

    public RedisSocketServer(RedisClient redisClient) {
        super(redisClient);
        connectionManager = new ServerConnectionManager(redisClient, commands, publisherCommand);
        allClients = new ArrayList<>();
    }

    @Override
    public void addConnectListener(ClientConnectListener connectListener) {
        connectionManager.listenConnectedClients(connectListener);
    }

    @Override
    public void addDisconnectedListener(ClientDisconnectListener disconnectListener) {
        connectionManager.listenDisconnectClients(disconnectListener);
    }

    public List<RedisIOClient> getAllClients() {
        Map<String, String> hgetall = commands.hgetall(SocketInfo.ALL_CLIENT.getValue());
        ObjectMapper mapper = new ObjectMapper();
        hgetall.forEach((key, value) -> {
            try {
                allClients.add(mapper.readValue(value, RedisIOClient.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return allClients;
    }


    @Override
    public void start() {
        new Thread(() -> {
            while (true) {
            }
        }).start();
    }
}
