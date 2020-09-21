package io.blt.socket.single;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.blt.client.ClientData;
import io.blt.listener.ClientConnectListener;
import io.blt.listener.ClientDisconnectListener;
import io.blt.listener.ClientListener;
import io.blt.manager.single.ServerConnectionManager;
import io.blt.socket.SocketInfo;
import io.lettuce.core.RedisClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisSocketServer extends SocketBase implements ClientListener {


    private ServerConnectionManager connectionManager;
    private List<ClientData> allClients;

    public RedisSocketServer(RedisClient redisClient) {
        super(redisClient);
        connectionManager = new ServerConnectionManager(redisClient, commands);
        allClients = new ArrayList<>();
    }

    @Override
    public void addConnectListener(ClientConnectListener connectListener) {
        connectionManager.listenConnectedClients(connectListener);
        try {
            connectionManager.manualConnectionControl(connectListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDisconnectedListener(ClientDisconnectListener disconnectListener) {
        connectionManager.listenDisconnectClients(disconnectListener);
    }

    public List<ClientData> getAllClients() {
        allClients.clear();
        Map<String, String> hgetall = commands.hgetall(SocketInfo.ALL_CLIENT.getValue());
        ObjectMapper mapper = new ObjectMapper();
        hgetall.forEach((key, value) -> {
            try {
                allClients.add(mapper.readValue(value, ClientData.class));
            } catch (IOException e) {
                System.out.println("Somethings went wrong when get all clients " + e.getMessage());
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
