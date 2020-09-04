package io.hubbox.socket;

import lombok.Getter;

/**
 * @author fatih
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
