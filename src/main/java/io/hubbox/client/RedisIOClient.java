package io.hubbox.client;

import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.Getter;
import lombok.ToString;

@ToString
public class RedisIOClient {

    @Getter
    private ClientData clientData;
    private RedisPubSubCommands<String, String> publisherCommand;


    public RedisIOClient(RedisPubSubCommands<String, String> publisherCommand) {
        clientData = new ClientData();
        this.publisherCommand = publisherCommand;
    }

    public void sendMessage(String channel, String message) {
        publisherCommand.publish(channel, message);
    }
}
