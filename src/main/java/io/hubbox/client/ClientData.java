package io.hubbox.client;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ToString
public class ClientData {
    @Getter
    private String sessionId;
    @Getter
    @Setter
    private String clientName;
    @Getter
    private Set<String> channels = new HashSet<>();

    ClientData() {
        sessionId = UUID.randomUUID().toString();
    }
}