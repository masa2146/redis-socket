package io.hubbox.manager;

import lombok.Getter;

/**
 * @author fatih
 */
public enum ClientInfo {
    STATUS("status"),
    INFO("info"),
    OK("1"),
    NOT_OK("0");

    @Getter
    private String value;

    ClientInfo(String value) {
        this.value = value;
    }
}
