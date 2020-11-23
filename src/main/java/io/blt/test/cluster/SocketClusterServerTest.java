package io.blt.test.cluster;

import io.blt.client.RedisIOClient;
import io.blt.listener.ClientDisconnectListener;
import io.blt.listener.EventListener;
import io.blt.socket.cluster.RedisClusterServerSocket;
import io.lettuce.core.RedisException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.output.KeyStreamingChannel;

import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletionException;

/**
 * @author fatih
 */
public class SocketClusterServerTest {

    public static void main(String[] args) {
        RedisURI node1 = RedisURI.create("172.16.255.192", 6379);
        RedisURI node2 = RedisURI.create("172.16.255.193", 6379);
        RedisURI node3 = RedisURI.create("172.16.255.194", 6379);
        RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));

        final ClusterTopologyRefreshOptions refreshOptions =
                ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(2))
                        .refreshTriggersReconnectAttempts(2)
                        .enablePeriodicRefresh(Duration.ofMinutes(10))
                        .build();
        redisClient.setDefaultTimeout(Duration.ofSeconds(10));
        redisClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(refreshOptions).build());
        redisClient.reloadPartitions();
        RedisClusterServerSocket server = new RedisClusterServerSocket(redisClient);

        server.start();


        server.addDisconnectedListener(new ClientDisconnectListener() {
            @Override
            public void onDisconnect(RedisIOClient client) {
                System.out.println("Disconnected Client: " + client.getClientData().getSessionId());
            }
        });

        server.addConnectListener(client -> {
            System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
            client.sendMessage("Ben Bağlandım.");

        });

        server.addEventListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("SERVER RECEIVED THE MESSAGE: " + message);
            }
        }, "message");


        new Thread(() -> {
            while (true) {
                System.out.println("Send Message...");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                server.sendMessage("asd", "asd");
                System.out.println("Message send");
            }
        }).start();
    }
}
