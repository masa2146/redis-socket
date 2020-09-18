package io.hubbox.test.cluster;

import io.hubbox.listener.EventListener;
import io.hubbox.listener.ServerConnectListener;
import io.hubbox.listener.ServerDisconnectListener;
import io.hubbox.socket.cluster.RedisClusterSocketClient;
import io.hubbox.socket.single.RedisSocketClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;

import java.util.Scanner;

/**
 * @author fatih
 */
public class SocketClusterClientTest {

    public static void main(String[] args) {
        RedisClusterClient redisClient = RedisClusterClient.create("redis://192.168.143.192:6380");
        RedisClusterSocketClient client = new RedisClusterSocketClient(redisClient);


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

        new Thread(() -> {
            System.out.println("Send Message...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            client.sendMessage("asd", "asd");

        }).start();
    }
}
