package io.blt.test.cluster;

import io.blt.client.RedisIOClient;
import io.blt.listener.ClientDisconnectListener;
import io.blt.listener.EventListener;
import io.blt.socket.cluster.RedisClusterServerSocket;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author fatih
 */
public class SocketClusterServerTest {

    public static void main(String[] args) {
        RedisURI node1 = RedisURI.create("192.168.143.192", 6379);
        RedisURI node2 = RedisURI.create("192.168.143.193", 6379);
        RedisURI node3 = RedisURI.create("192.168.143.194", 6379);
        RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
        RedisClusterServerSocket server = new RedisClusterServerSocket(redisClient);

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
