package rarus.chat._2_webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rarus.chat._3_service.ChatService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import rarus.chat.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
public class ChatWebSocket {

    private final ChatService chatService;
    private Session session;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean authorized = false;
    private String expectedKey = "login";
    private String userName = null;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public ChatWebSocket(ChatService chatService) {
        this.chatService = chatService;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        chatService.add(this);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        Map<String, String> map = null;
        try {
            map = objectMapper.readValue(data, Map.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String value = map.get(expectedKey);
        if (value!=null && !value.isEmpty()){
            if (authorized) {
                Message message = new Message(
                        userName,
                        now().format(dateTimeFormatter),
                        value);
// TODO: this store to redis
                chatService.sendMessage(data);
            } else {
                userName = value;
                authorized = true;
                expectedKey = "message";
            }
        }
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
