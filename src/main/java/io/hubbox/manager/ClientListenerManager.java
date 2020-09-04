package io.hubbox.manager;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.AllArgsConstructor;
import lombok.ToString;

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
