# Redis Socket

## Başlangıç
Redis socket, Redis yüklü cihazlar arasında veri haberleşmesini kolaylaştırmak için geliştirilmiştir.

## Nasıl Çalışır?
**Redis Socket** hem sunucu hem de istemci olarak kullanılabilir. Redis üzerinden *PubSub* metotlarını kullanalarak
mesaj iletimini ve gönderilen mesajın okunması işlemini gerçekleştirir. 

### Redis Socket Server
*Redis Socket Server* bir web sunucu soketi gibi işlem yapar. Kendisine bağlanan *Redis Socket Client*'ların listesini tutar. 
İstemciler *Redis Socket Server*'a bağlandığında, bu bağlantıyı yakalar. Ayrıca koparılan bağlantılarıda yakalar.
İstemcilerin herbirine mesaj gönderebilir.

## Örnek
### *Single Redis Socket* Kullanımı

#### Sunucu

Bağlantıların oluşturulması

```java
RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
RedisServerSocket server = new RedisSingleServerSocket(redisClient);
```
Sunucuyu başlat 

```java
server.start();
```

Bağlanan istemcileri dinle
```java
server.addConnectListener(client -> {
    System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
});
```

Bağlanan istemcilere direk mesaj atabilirsin.

```java
server.addConnectListener(client -> {
    System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
    client.sendMessage("You connected.");

});
``` 

Bağlantısı kopan istemcileri yakalayabilirsin.

```java
server.addDisconnectedListener(new ClientDisconnectListener() {
    @Override
    public void onDisconnect(RedisIOClient client) {
        System.out.println("Disconnected Client: " + client.getClientData().getSessionId());
    }
});
```

Özel bir mesaj kanalını dinleyebilirsin. Bu örnekte *message* kanalına gelen istekler yakalanmaktadır.

```java
server.addEventListener(new EventListener() {
    @Override
    public void onMessage(String channel, String message) {
        System.out.println("SERVER RECEIVED THE MESSAGE: " + message);
    }
}, "message");
```

İstenilen bir kanala mesaj atabilirsin.


```java
 server.sendMessage("channel1", "message");
``` 

Tüm kodlar ile birlikte basit bir örnek

```java
public class SocketServerTest {

    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisServerSocket server = new RedisSingleServerSocket(redisClient);

        server.start();

        server.addConnectListener(client -> {
            System.out.println("Client connected. Client id is " + client.getClientData().getSessionId());
            client.sendMessage("Ben Bağlandım.");

        });

        server.addDisconnectedListener(new ClientDisconnectListener() {
            @Override
            public void onDisconnect(RedisIOClient client) {
                System.out.println("Disconnected Client: " + client.getClientData().getSessionId());
            }
        });

        server.addEventListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("SERVER RECEIVED THE MESSAGE: " + message);
            }
        }, "message");

// Enter tuşuna bastıkça mesaj gönderecektir.
        new Thread(() -> {
            System.out.println("Send Message...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            try {
                server.sendMessage("channel1", "message");
            } catch (SendMessageException e) {
                e.printStackTrace();
            }

        }).start();
    }

}
```


#### İstemci
Bağlantıların oluşturulması
 
 ```java
RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
RedisClientSocket client = new RedisSingleClientSocket(redisClient);
```

Sunucuya bağlandığının kontrol edilmesi işlemi 

```java
client.addConnectListener(new ServerConnectListener() {
    @Override
    public void onConnectedToServer() {
        System.out.println("I connected to server");
    }
});
```

İstemcinin Redis ile bağlantısının koptuğunu yakalamak için

```java
client.addDisconnectListener(new ServerDisconnectListener() {
    @Override
    public void onDisconnectedServer() {
        System.out.println("Client disconnected");
    }
});
```

İstemcinin sadece kendisine gönderilen mesajları dinleme işlemi

 ```java
client.addSelfMessageListener(new EventListener() {
    @Override
    public void onMessage(String channel, String message) {
        System.out.println("SELF MESSAGE CONTROL! " + message);
    }
});
```

Özel bir mesaj kanalı dinlenmesi

```java
client.addEventListener(new EventListener() {
    @Override
    public void onMessage(String channel, String message) {
        System.out.println("CHANNEL: " + channel + " Message: " + message);
    }
}, "marmara");
```

İstemciyi çalıştır

```java
client.start();
```

Tüm kodları

```java
public class SocketClientTest {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://192.168.143.192:6380");
        RedisClientSocket client = new RedisSingleClientSocket(redisClient);


        client.addConnectListener(new ServerConnectListener() {
            @Override
            public void onConnectedToServer() {
                System.out.println("Client connected");
            }
        });

        client.addDisconnectListener(new ServerDisconnectListener() {
            @Override
            public void onDisconnectedServer() {
                System.out.println("Client disconnected");
            }
        });

        client.addSelfMessageListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("SELF MESSAGE CONTROL! " + message);
            }
        });

        client.start();

        client.addEventListener(new EventListener() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("CHANNEL: " + channel + " Message: " + message);
            }
        }, "marmara");

        new Thread(() -> {
            System.out.println("Send Message...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            try {
                client.sendMessage("channel1", "message");
            } catch (SendMessageException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
```

### *Cluster Redis Socket* Kullanımı

Cluster yapısında tek farklılık bağlantının oluşturulmasındadır.

#### Sunucu
```java
RedisURI node1 = RedisURI.create("192.168.143.192", 6379);
RedisURI node2 = RedisURI.create("192.168.143.193", 6379);
RedisURI node3 = RedisURI.create("192.168.143.194", 6379);
RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
RedisClusterServerSocket server = new RedisClusterServerSocket(redisClient);
```

#### İstemci
```java
RedisURI node1 = RedisURI.create("192.168.143.192", 6379);
RedisURI node2 = RedisURI.create("192.168.143.193", 6379);
RedisURI node3 = RedisURI.create("192.168.143.194", 6379);
RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
RedisClusterClientSocket client = new RedisClusterClientSocket(redisClient);
```

