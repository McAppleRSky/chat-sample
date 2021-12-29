package rarus.chat._2_webSocket;

import rarus.chat._3_service.ChatService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
public class ChatWebSocket {

    private final ChatService chatService;
    private Session session;

    public ChatWebSocket(ChatService chatService) {
        this.chatService = chatService;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        chatService.add(this);
        Map<String, List<String>> parameterMap = session.getUpgradeRequest().getParameterMap();
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        LocalDateTime time = now();
        chatService.sendMessage(data);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        chatService.remove(this);
    }

    public void sendToClientString(String data) {
        try {
            session.getRemote().sendString(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
