package com.example.app;
import org.java_websocket.client.WebSocketClient;

public class MyWebSocketClient {
    private static MyWebSocketClient instance;
    private WebSocketClient webSocketClient;
    private boolean isConnected = false;

    private MyWebSocketClient() {
    }

    public static synchronized MyWebSocketClient getInstance() {
        if (instance == null) {
            instance = new MyWebSocketClient();
        }
        return instance;
    }
    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean connected) {
        isConnected = connected;
    }
    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient client) {
        this.webSocketClient = client;
    }

    public void deleteWebSocketClient(){
        setWebSocketClient(null);
    }
}
