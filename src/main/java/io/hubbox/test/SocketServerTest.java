package io.hubbox.test;

import io.hubbox.listener.EventListener;
import io.hubbox.listener.ServerConnectListener;
import io.hubbox.listener.ServerDisconnectListener;
import io.hubbox.socket.RedisSocketClient;
import io.lettuce.core.RedisClient;

public class SocketServerTest {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisSocketClient client = new RedisSocketClient(redisClient);

        client.addEventListener("marmara", new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("CHANNEL: " + channel + " Message: " + message);
            }

            @Override
            public void onAddChannel(String channel, long l) {

            }

            @Override
            public void onRemoveChannel(String channel, long l) {

            }
        });

        client.addConnectListener(new ServerConnectListener() {
            @Override
            public void onConnectedToServer() {
                System.out.println("Client connected");
            }
        });

        client.addDisconnectListener(new ServerDisconnectListener() {
            @Override
            public void onDisconnectedServer() {

            }
        });

        client.start();
    }
}
