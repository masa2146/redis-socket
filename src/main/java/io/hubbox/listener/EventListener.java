package io.hubbox.listener;

/**
 * Detect message, add channel and remove channel for client socket
 */
public interface EventListener {

    void onMessage(String channel, String message);

    void onAddChannel(String channel, long l);

    void onRemoveChannel(String channel, long l);
}
