package io.hubbox.listener;

import io.hubbox.client.RedisIOClient;


/**
 * When client connected to server this function will trigger
 */
public interface ClientDisconnectListener {
    void onDisconnect(RedisIOClient client);
}
