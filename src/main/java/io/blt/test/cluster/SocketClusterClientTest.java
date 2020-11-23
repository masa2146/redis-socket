package io.blt.test.cluster;

import io.blt.listener.EventListener;
import io.blt.listener.ServerConnectListener;
import io.blt.listener.ServerDisconnectListener;
import io.blt.socket.cluster.RedisClusterClientSocket;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author fatih
 */
public class SocketClusterClientTest {

    public static void main(String[] args) {
        RedisURI node1 = RedisURI.create("172.16.255.192", 6379);
        RedisURI node2 = RedisURI.create("172.16.255.193", 6379);
        RedisURI node3 = RedisURI.create("172.16.255.194", 6379);
        RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
        RedisClusterClientSocket client = new RedisClusterClientSocket(redisClient);


        client.addConnectListener(new ServerConnectListener() {
            @Override
            public void onConnectedToServer() {
                System.out.println("Client connected");
            }
        });

        client.addDisconnectListener(new ServerDisconnectListener() {
            @Override
            public void onDisconnectedServer() {
                System.out.println("Client disconnected");
            }
        });

        client.addSelfMessageListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("SELF MESSAGE CONTROL! " + message);
            }
        });

        client.start();

        client.addEventListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("CHANNEL: " + channel + " Message: " + message);
            }
        }, "marmara");

        client.addEventListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("MESSAGE: " + message);
            }
        }, "asd");

        new Thread(() -> {
            System.out.println("Send Message...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
//            client.sendMessage("asd", "asd");


        }).start();
    }
}
