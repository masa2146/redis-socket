package io.hubbox.client;

import io.hubbox.listener.EventListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class RedisIOClient implements RedisPubSubListener<String, String> {

    @Getter
    @Setter
    private ClientData clientData;
    private RedisPubSubCommands<String, String> publisherCommand;
    private RedisPubSubCommands<String, String> subscriberCommand;
    private StatefulRedisPubSubConnection<String, String> subConnection;
    private EventListener eventListener;

    public RedisIOClient(RedisClient redisClient) {
        this.clientData = new ClientData();
        this.subConnection = redisClient.connectPubSub();
        this.publisherCommand = redisClient.connectPubSub().sync();
    }

    public void addMessageListener(EventListener eventListener) {
        this.eventListener = eventListener;
        this.subConnection.addListener(this);
        this.subscriberCommand = subConnection.sync();
        this.subscribeToSelf();
    }

    /**
     * When the class initialize then subscribe to self session id.
     * Thus when it will send message to itself
     */
    private void subscribeToSelf() {
        System.out.println("Subscribing to self... " + clientData.getSessionId());
        this.subscriberCommand.subscribe(clientData.getSessionId());
    }

    public void sendMessage(String message) {
        this.publisherCommand.publish(clientData.getSessionId(), message);

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
