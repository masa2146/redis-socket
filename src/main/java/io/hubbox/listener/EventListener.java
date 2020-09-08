package io.hubbox.listener;

/**
 * Detect message, add channel and remove channel for client socket
 */
public interface EventListener {

    /**
     * When message received, this function will trigger.
     * @param message is received data
     * @param channel Message received via this channel.
     */
    void onMessage(String channel, String message);
}
