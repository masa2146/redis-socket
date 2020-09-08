package io.hubbox.manager;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * This class provides open new channel to added each one event
 * Example, when add new event listener then will create new Redis PubSub connection.
 * And the connection subscribe to the added event name
 */
@AllArgsConstructor
@ToString
public class ClientListenerManager {
    private MessageManager messageManager;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;

    public void removeEvent(String channel) {
        pubSubConnection.sync().unsubscribe(channel);
        pubSubConnection.removeListener(messageManager);
    }

}
