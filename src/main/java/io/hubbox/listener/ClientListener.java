package io.hubbox.listener;

/**
 * Handle the connected client to server
 */
public interface ClientListener {

    void addConnectListener(ClientConnectListener connectListener);
}
