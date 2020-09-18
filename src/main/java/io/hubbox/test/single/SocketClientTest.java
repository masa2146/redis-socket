package io.hubbox.test.single;

import io.hubbox.listener.EventListener;
import io.hubbox.listener.ServerConnectListener;
import io.hubbox.listener.ServerDisconnectListener;
import io.hubbox.socket.single.RedisSocketClient;
import io.lettuce.core.RedisClient;

import java.util.Scanner;

public class SocketClientTest {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisSocketClient client = new RedisSocketClient(redisClient);


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
