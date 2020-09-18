package io.hubbox.client;

import io.hubbox.listener.EventListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.sync.RedisClusterPubSubCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.Getter;
import lombok.Setter;

public class RedisIOClient implements RedisPubSubListener<String, String> {

    @Getter
    @Setter
    private ClientData clientData;

    /*** ========================== SINGLE STRUCTURE ========================== */
    private RedisClient redisClient;
    private RedisPubSubCommands<String, String> publisherCommand;
    private RedisPubSubCommands<String, String> subscriberCommand;
    private StatefulRedisPubSubConnection<String, String> subConnection;

    /*** ========================== CLUSTER STRUCTURE ========================== */
    private RedisClusterClient redisClusterClient;
    private RedisClusterPubSubCommands<String, String> publisherClusterCommand;
    private RedisClusterPubSubCommands<String, String> subscriberClusterCommand;
    private StatefulRedisClusterPubSubConnection<String, String> subClusterConnection;

    private EventListener eventListener;

    public RedisIOClient(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.clientData = new ClientData();
        this.subConnection = redisClient.connectPubSub();
        this.publisherCommand = redisClient.connectPubSub().sync();
    }

    public RedisIOClient(RedisClusterClient redisClient) {
        this.redisClusterClient = redisClient;
        this.clientData = new ClientData();
        this.subClusterConnection = redisClient.connectPubSub();
        this.publisherClusterCommand = redisClient.connectPubSub().sync();
    }

    public void addMessageListener(EventListener eventListener) {
        this.eventListener = eventListener;
        if (redisClient != null) {
            this.subConnection.addListener(this);
            this.subscriberCommand = subConnection.sync();
        } else if (redisClusterClient != null) {
            this.subClusterConnection.addListener(this);
            this.subscriberClusterCommand = this.subClusterConnection.sync();
        }
        this.subscribeToSelf();
    }

    /**
     * When the class initialize then subscribe to self session id.
     * Thus when it will send message to itself
     */
    private void subscribeToSelf() {
        System.out.println("Subscribing to self... " + clientData.getSessionId());
        if (redisClient != null) {
            this.subscriberCommand.subscribe(clientData.getSessionId());
        } else if (redisClusterClient != null) {
            this.subscriberClusterCommand.subscribe(clientData.getSessionId());
        }
    }

    public void sendMessage(String message) {
        if (redisClient != null) {
            this.publisherCommand.publish(clientData.getSessionId(), message);
        } else if (redisClusterClient != null) {
            this.publisherClusterCommand.publish(clientData.getSessionId(), message);
        }

    }

    @Override
    public void message(String s, String s2) {
        this.eventListener.onMessage(s, s2);
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
}
