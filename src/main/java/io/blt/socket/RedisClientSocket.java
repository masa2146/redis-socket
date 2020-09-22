package io.blt.socket;

import io.blt.client.RedisIOClient;
import io.blt.listener.EventListener;
import io.blt.listener.MessageListener;
import io.blt.listener.ServerListener;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

/**
 * @author fatih
 */
public interface RedisClientSocket extends MessageListener, ServerListener {

    void addSelfMessageListener(EventListener eventListener);

    RedisIOClient getRedisIOClient();

    RedisClusterCommands<String, String> getCommands();

    void start();
}
