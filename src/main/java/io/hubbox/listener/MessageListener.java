package io.hubbox.listener;

public interface MessageListener {

    void addEventListener(String channel, EventListener eventListener);

    void removeEventListener(String channel);

    <T> void sendMessage(String channel, Class<T> message);

    void sendMessage(String channel, String message);
}
