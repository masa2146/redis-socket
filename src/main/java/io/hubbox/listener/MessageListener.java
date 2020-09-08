package io.hubbox.listener;

import io.hubbox.exceptions.SendMessageException;

public interface MessageListener {

    void addEventListener(String channel, EventListener eventListener);

    void removeEventListener(String channel);

    <T> void sendMessage(String channel, Class<T> message) throws SendMessageException;

    void sendMessage(String channel, String message) throws SendMessageException;
}
