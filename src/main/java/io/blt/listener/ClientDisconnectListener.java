package io.blt.listener;

import io.blt.client.RedisIOClient;


/**
 * When client connected to server this function will trigger
 */
public interface ClientDisconnectListener {
    void onDisconnect(RedisIOClient client);
}
