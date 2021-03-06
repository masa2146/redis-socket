package io.blt.listener;

/**
 * Handle the connected client to server
 */
public interface ClientListener {

    void addConnectListener(ClientConnectListener connectListener);

    void addDisconnectedListener(ClientDisconnectListener disconnectListener);
}
