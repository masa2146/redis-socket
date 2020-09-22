package io.blt.test.single;

import io.blt.exceptions.SendMessageException;
import io.blt.listener.EventListener;
import io.blt.listener.ServerConnectListener;
import io.blt.listener.ServerDisconnectListener;
import io.blt.socket.RedisClientSocket;
import io.blt.socket.single.RedisSingleClientSocket;
import io.lettuce.core.RedisClient;

import java.util.Scanner;

public class SocketClientTest {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisClientSocket client = new RedisSingleClientSocket(redisClient);


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
            try {
                client.sendMessage("asd", "asd");
            } catch (SendMessageException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
