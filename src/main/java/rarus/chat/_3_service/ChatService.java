package rarus.chat._3_service;

import rarus.chat._2_webSocket.WebSocketChatInstance;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface ChatService {

    void distributionToClients(String data);

    void add(WebSocketChatInstance webSocket);

    void remove(WebSocketChatInstance webSocket);

    int clientCount();

    Set<String> clientsNames();

    boolean clientAbsent(String name);

}
