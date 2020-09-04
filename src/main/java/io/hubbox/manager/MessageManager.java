package io.hubbox.manager;

import io.hubbox.listener.EventListener;
import io.lettuce.core.pubsub.RedisPubSubListener;

public class MessageManager implements RedisPubSubListener<String, String> {

    private EventListener eventListener;

    public MessageManager(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void message(String s, String s2) {
        eventListener.onMessage(s, s2);

    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void subscribed(String s, long l) {
        eventListener.onAddChannel(s, l);
    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {
        eventListener.onRemoveChannel(s, l);
    }

    @Override
    public void punsubscribed(String s, long l) {

    }
}
