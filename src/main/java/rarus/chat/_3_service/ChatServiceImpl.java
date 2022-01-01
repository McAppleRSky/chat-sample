package rarus.chat._3_service;

import rarus.chat._2_webSocket.WebSocketChatInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServiceImpl implements ChatService{

    private Set<WebSocketChatInstance> webSockets;

    public ChatServiceImpl() {
        this.webSockets = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void distributionToClients(String data) {
        for (WebSocketChatInstance client : webSockets) {
            try {
                client.sendToClient(data);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void add(WebSocketChatInstance webSocket) {
        webSockets.add(webSocket);
    }

    @Override
    public void remove(WebSocketChatInstance webSocket) {
        webSockets.remove(webSocket);
    }

    @Override
    public int clientCount(){
        return webSockets.size();
    }

    @Override
    public Set<String> clientsNames() {
        Set<String> names = new HashSet<>();
        for (WebSocketChatInstance socket : webSockets) {
            names.add(socket.getClientName());
        }
        return names;
    }

    @Override
    public boolean clientAbsent(String name) {
        for (WebSocketChatInstance client : webSockets) {
            if (client.getClientName().equals(name)) {
                return false;
            }
        }
        return true;
    }

}
