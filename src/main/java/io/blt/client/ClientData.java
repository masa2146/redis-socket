package io.blt.client;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ToString
public class ClientData {
    @Getter
    @Setter
    private String sessionId;
    @Getter
    @Setter
    private String clientName;
    @Getter
    @Setter
    private Set<String> channels = new HashSet<>();

    ClientData() {
        this.sessionId = UUID.randomUUID().toString();
    }
}