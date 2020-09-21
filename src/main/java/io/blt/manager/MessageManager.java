package io.blt.manager;

import io.blt.listener.EventListener;
import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * @author fatih
 * This class receive message from Redis PubSub channels.
 * The recevied messages are sent via {@link EventListener}
 */
public class MessageManager implements RedisPubSubListener<String, String> {

    private EventListener eventListener;

    public MessageManager(EventListener eventListener) {
        this.eventListener = eventListener;
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
