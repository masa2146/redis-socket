package io.blt.manager;

import lombok.Getter;

/**
 * @author fatih
 * <p>
 * This enum contains control keys and values.
 * Example, check client 'status' field in Redis hash map
 * If Client is connected then  'status' field is 1 else 'status' field is 0.
 * Also hash map  of the Redis keeps the clients info as String. The data is in 'info' field.
 * </p>
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
