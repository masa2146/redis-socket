package io.hubbox.client;

import lombok.Getter;
import lombok.ToString;

@ToString
public class RedisIOClient {

    @Getter
    private ClientData clientData;


    public RedisIOClient() {
        clientData = new ClientData();
    }

    public void sendMessage(String channel, String message) {

    }
}
