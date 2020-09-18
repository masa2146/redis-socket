package io.hubbox.test.cluster;

import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ClientDisconnectListener;
import io.hubbox.listener.EventListener;
import io.hubbox.socket.cluster.RedisClusterSocketServer;
import io.hubbox.socket.single.RedisSocketServer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;

import java.util.Scanner;

/**
 * @author fatih
 */
public class SocketClusterServerTest {

    public static void main(String[] args) {
        RedisClusterClient redisClient = RedisClusterClient.create("redis://192.168.143.192:6380");
        RedisClusterSocketServer server = new RedisClusterSocketServer(redisClient);

        server.start();

        server.addConnectListener(client -> {
            System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
            client.sendMessage("Ben Bağlandım.");

        });

        server.addDisconnectedListener(new ClientDisconnectListener() {
            @Override
            public void onDisconnect(RedisIOClient client) {
                System.out.println("Disconnected Client: " + client.getClientData().getSessionId());
            }
        });

        server.addEventListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("SERVER RECEIVED THE MESSAGE: " + message);
            }
        }, "message");


        new Thread(() -> {
            System.out.println("Send Message...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            server.sendMessage("asd", "asd");

        }).start();
    }
}
