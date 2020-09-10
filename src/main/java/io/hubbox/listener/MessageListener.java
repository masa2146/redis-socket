package io.hubbox.listener;

import io.hubbox.exceptions.SendMessageException;

public interface MessageListener {

    void addEventListener(EventListener eventListener, String... channel);

    void removeEventListener(String channel);

    <T> void sendMessage(String channel, Class<T> message) throws SendMessageException;

    void sendMessage(String channel, String message) throws SendMessageException;
}
