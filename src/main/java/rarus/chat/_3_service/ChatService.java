package rarus.chat._3_service;

import rarus.chat._2_webSocket.WebSocketChatInstance;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
// https://github.com/rarus/middle-java-developer
// https://ru.stackoverflow.com/questions/583714/websocket-%D0%98%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D1%8F-%D0%9C%D0%B5%D1%85%D0%B0%D0%BD%D0%B8%D0%B7%D0%BC-%D1%80%D0%B0%D1%81%D0%BF%D1%80%D0%B5%D0%B4%D0%B5%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F
// https://learn.javascript.ru/websockets
public class ChatService {

    private Set<WebSocketChatInstance> webSockets;

    public ChatService() {
        this.webSockets = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public void sendMessage(String data) {
        for (WebSocketChatInstance user : webSockets) {
            try {
                user.sendToClientString(data);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void add(WebSocketChatInstance webSocket) {
        webSockets.add(webSocket);
    }

    public void remove(WebSocketChatInstance webSocket) {
        webSockets.remove(webSocket);
    }

}
