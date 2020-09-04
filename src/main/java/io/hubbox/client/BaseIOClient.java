package io.hubbox.client;

import lombok.Getter;
import lombok.ToString;

@ToString
public class BaseIOClient {
    @Getter
    private ClientData clientData;

    BaseIOClient() {
        clientData = new ClientData();
    }

    public void sendMessage(String channel, String message){

    }

}
