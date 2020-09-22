package io.blt.socket;

import io.blt.listener.ClientListener;
import io.blt.listener.MessageListener;
import io.lettuce.core.api.sync.BaseRedisCommands;

/**
 * @author fatih
 */
public interface RedisServerSocket extends MessageListener, ClientListener {
    BaseRedisCommands<String, String> getCommands();


    void start();

}
