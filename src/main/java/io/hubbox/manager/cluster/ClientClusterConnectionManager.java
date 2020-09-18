package io.hubbox.manager.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hubbox.client.RedisIOClient;
import io.hubbox.listener.ServerConnectListener;
import io.hubbox.listener.ServerDisconnectListener;
import io.hubbox.socket.SocketInfo;
import io.lettuce.core.RedisChannelHandler;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionStateListener;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.sync.RedisClusterPubSubCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

/**
 * @author fatih
 * This class controls the client connect and disconnect to the server.
 * Run on client side.
 */
public class ClientClusterConnectionManager {

    private RedisClusterClient redisClient;
    private RedisIOClient client;
    private RedisClusterPubSubCommands<String, String> publisherCommand;

    private ObjectMapper objectMapper;
    private StatefulRedisClusterPubSubConnection<String, String> subConnection;

    public ClientClusterConnectionManager(RedisClusterClient redisClient, RedisIOClient client, RedisClusterPubSubCommands<String, String> publisherCommand) {
        this.redisClient = redisClient;
        this.objectMapper = new ObjectMapper();
        this.client = client;
        this.publisherCommand = publisherCommand;
        subConnection = redisClient.connectPubSub();
    }

    /**
     * The function listens to 'onConnected/<ClientId>' channel.
     * And it sends message to "onConnected" channel on server. Server return message to 'onConnected/<ClientId>'
     * in this way the client receives the return message and connects.
     */
    public void checkConnection(ServerConnectListener connectListener) throws JsonProcessingException {
        this.subConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String s, String s2) {
                connectListener.onConnectedToServer();
            }

            @Override
            public void message(String s, String k1, String s2) {

            }

            @Override
            public void subscribed(String s, long l) {

            }

            @Override
            public void psubscribed(String s, long l) {

            }

            @Override
            public void unsubscribed(String s, long l) {

            }

            @Override
            public void punsubscribed(String s, long l) {

            }
        });
        this.subConnection.sync().subscribe(SocketInfo.EVENT_CONNECTED.name() + "/" + client.getClientData().getSessionId());

        publisherCommand.publish(SocketInfo.EVENT_CONNECTED.name(), objectMapper.writeValueAsString(client.getClientData()));
    }

    /**
     * When the client disconnected to REDIS server then trigger the disconnected function.
     */
    public void checkDisconnectedListener(ServerDisconnectListener disconnectListener) {
        redisClient.addListener(new RedisConnectionStateListener() {
            @Override
            public void onRedisDisconnected(RedisChannelHandler<?, ?> redisChannelHandler) {
                disconnectListener.onDisconnectedServer();
            }

            @Override
            public void onRedisExceptionCaught(RedisChannelHandler<?, ?> redisChannelHandler, Throwable throwable) {

            }
        });
    }
}
