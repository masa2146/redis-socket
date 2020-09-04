package io.hubbox.socket;

import lombok.Getter;

/**
 * @author fatih
 * This enum contains connection status and some specific fields in order to subscribe and publish data to these fields.
 * Example, If client started publish data to 'onConnected' field on server. Then the server received the published data.
 * And set the data to 'allClient' key of the hash map.
 */
public enum SocketInfo {
    EVENT_CONNECTED("onConnected"),
    EVENT_DISCONNECTED("onDisconnected"),
    ALL_CLIENT("allClient");

    @Getter
    private String value;

    SocketInfo(String value) {
        this.value = value;
    }
}
