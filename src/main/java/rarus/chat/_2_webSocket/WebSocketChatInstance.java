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
import rarus.chat.main.Main;
import rarus.chat.model.Message;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
public class WebSocketChatInstance {

    private final ChatService chatService;
    private Session session;
    private int room;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean authorized = false;
    private String expectedKey = "login";
    private String userName = null;
    private final DateTimeFormatter dateTimeFormatter = (DateTimeFormatter)Main.context.get(DateTimeFormatter.class);
    private Jedis jedis = ((JedisPool)Main.context.get(JedisPool.class)).getResource();

    public WebSocketChatInstance(ChatService chatService, int room) {
        this.chatService = chatService;
        this.room = room;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        chatService.add(this);
        this.session = session;
    }


    // https://www.baeldung.com/jedis-java-redis-client-library

    @OnWebSocketMessage
    public void onMessage(String data) {
        Map<String, String> map;
        try {
            map = objectMapper.readValue(data, Map.class);
            String value = map.get(expectedKey);
            if (value!=null && !value.isEmpty()){
                if (authorized) {
                    jedis.lpush(
                            objectMapper.writeValueAsString(
                                    new Message(
                                            userName,
                                            now().format(dateTimeFormatter),
                                            value) ) );
                    chatService.sendMessage(
                            jedis.rpop(
                                    Integer.toString(room)));
                } else {
                    userName = value;
                    authorized = true;
                    expectedKey = "message";
                }
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
