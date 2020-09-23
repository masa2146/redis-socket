package io.blt.test.single;

import io.blt.exceptions.SendMessageException;
import io.blt.socket.RedisServerSocket;
import io.blt.socket.single.RedisSingleServerSocket;
import io.lettuce.core.RedisClient;

import java.util.Scanner;

/**
 * @author fatih
 */
public class SocketServerTest {

    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisServerSocket server = new RedisSingleServerSocket(redisClient);

        server.start();

        server.addConnectListener(client -> {
            System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
            client.sendMessage("Ben Bağlandım.");

        });

        server.addDisconnectedListener(client -> System.out.println("Disconnected Client: " + client.getClientData().getSessionId()));

        server.addEventListener((channel, message) -> System.out.println("SERVER RECEIVED THE MESSAGE: " + message), "message");


        new Thread(() -> {
            System.out.println("Send Message...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            try {
                server.sendMessage("channel1", "message");
            } catch (SendMessageException e) {
                e.printStackTrace();
            }

        }).start();
    }

}
