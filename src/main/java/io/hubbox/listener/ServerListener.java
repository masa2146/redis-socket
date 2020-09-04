package io.hubbox.listener;

/**
 * Client sunucuya bağlanırsa 'addConnectListener' tetiklenecektir.
 * Eğer redis server socket kapaınrsa veya client bağlantısı giderse client tarafında  'addDisconnectListener' fonsiyonu tetiklenecektir.
 */
public interface ServerListener {
    void addConnectListener(ServerConnectListener connectListener);

    void addDisconnectListener(ServerDisconnectListener disconnectListener);


}
