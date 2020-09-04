package io.hubbox.test;

import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ClientConnectListener;
import io.hubbox.listener.EventListener;
import io.hubbox.socket.RedisSocketServer;
import io.lettuce.core.RedisClient;

/**
 * @author fatih
 */
public class SocketServerTest {

    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisSocketServer server = new RedisSocketServer(redisClient);

        server.addConnectListener(new ClientConnectListener() {
            @Override
            public void onConnect(RedisIOClient client) {
                System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
            }
        });

        server.addEventListener("message", new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("SERVER RECEIVED THE MESSAGE: " + message);
            }

            @Override
            public void onAddChannel(String channel, long l) {

            }

            @Override
            public void onRemoveChannel(String channel, long l) {

            }
        });

        server.start();
    }

}
