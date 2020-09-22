package io.blt.socket.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.blt.client.ClientData;
import io.blt.listener.ClientConnectListener;
import io.blt.listener.ClientDisconnectListener;
import io.blt.manager.cluster.ServerClusterConnectionManager;
import io.blt.socket.RedisServerSocket;
import io.blt.socket.SocketInfo;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fatih
 */
public class RedisClusterServerSocket extends ClusterBaseSocket implements RedisServerSocket {

    private ServerClusterConnectionManager connectionManager;
    private List<ClientData> allClients;

    public RedisClusterServerSocket(RedisClusterClient redisClient) {
        super(redisClient);
        connectionManager = new ServerClusterConnectionManager(redisClient, commands);
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

    @Override
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
    public RedisClusterCommands<String, String> getCommands() {
        return commands;
    }

    @Override
    public void start() {
        new Thread(() -> {
            while (true) {
                // PASS
            }
        }).start();
    }
}
