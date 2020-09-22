package io.blt.socket;

import io.blt.client.ClientData;
import io.blt.listener.ClientListener;
import io.blt.listener.MessageListener;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

import java.util.List;

/**
 * @author fatih
 */
public interface RedisServerSocket extends MessageListener, ClientListener {
    RedisClusterCommands<String, String> getCommands();

    List<ClientData> getAllClients();

    void start();

}
