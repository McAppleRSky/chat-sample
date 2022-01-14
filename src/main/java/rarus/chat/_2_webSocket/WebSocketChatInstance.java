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
import rarus.chat.Main;
import rarus.chat.model.Message;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.LocalDateTime.now;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
//@Log4j
public class WebSocketChatInstance {

    private final ChatService chatService;
    private Session session;
    private String room;
    private final String archRoom;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean authorized = false;
    private String expectedKey = "login";
    private String clientName = "";
    private final DateTimeFormatter dateTimeFormatter = (DateTimeFormatter) Main.context.get(DateTimeFormatter.class);
    private final JedisPool jedisPool = (JedisPool) Main.context.get(JedisPool.class);

    public String getClientName() {
        return clientName;
    }

    public WebSocketChatInstance(String room) {
        this.chatService = (ChatService)Main.context.get(ChatService.class);
        this.room = room;
        this.archRoom = room + "-arch";
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        chatService.add(this);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        Map<String, String> map;
        String now = now().format(dateTimeFormatter);
        try {
            map = objectMapper.readValue(data, Map.class);
            String value = map.get(expectedKey);
            if (value != null && !value.isEmpty()) {
                if (authorized) {
                    String[] values = value.split(" ");
                    switch (values[0]) {
                        case ("help"):
                            {
                                sendToClient(
                                        objectMapper.writeValueAsString(
                                                new Message(
                                                        "",
                                                        now,
                                                        "UserList - show active clients;\n" +
"ShowHistory N - show last N messages of chat with sending time and authors name."
                                                ) ) );
                            }
                            break;
                        case ("UserList"):
                            {
                                Set<String> clientsNames = chatService.clientsNames();
                                clientsNames.remove(clientName);
                                StringBuilder text = new StringBuilder("UserList");
                                text.append(":");
                                if (clientsNames.isEmpty()) {
                                    text.append(" ")
                                            .append("nobody");
                                } else {
                                    boolean first = true;
                                    for (String name : clientsNames) {
                                        if (first) {
                                            first = false;
                                            text.append("\n")
                                                    .append(name);
                                        } else {
                                            text.append(", ")
                                                    .append(name);
                                        }
                                    }
                                }
                                sendToClient(
                                        objectMapper.writeValueAsString(
                                                new Message(
                                                        "",
                                                        now,
                                                        text.toString()
                                                ) ) );
                            }
                            break;
                        case ("ShowHistory"):
                            {
                                int c = 0;
                                String notParseInt = null;
                                if (values.length == 2) {
                                    try {
                                        c = Integer.parseInt(values[1]);
                                    } catch (NumberFormatException e) {
                                        notParseInt = e.getMessage();
                                    }
                                    sendToClient(
                                            objectMapper.writeValueAsString(
                                                    new Message(
                                                            "",
                                                            now,
                                                            "ShowHistory: Last " + values[1] + ':'
                                                             + (notParseInt == null ? "" : notParseInt) ) ) );
                                    try (Jedis jedis = jedisPool.getResource()) {
                                        List<String> lastMessages = jedis.lrange(archRoom, 0, c - 1);
                                        c = lastMessages.size();
                                        for (int i = c; --i >= 0;) {
                                            sendToClient(lastMessages.get(i));
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            {
                                try (Jedis jedis = jedisPool.getResource()) {
                                    jedis.lpush(
                                            room,
                                            objectMapper.writeValueAsString(
                                                    new Message(
                                                            clientName,
                                                            now,
                                                            value)));
                                }
                                String distributingMessage = null;
                                try (Jedis jedis = jedisPool.getResource()) {
                                    distributingMessage = jedis.rpop(room);
                                    chatService.distributionToClients(
                                            distributingMessage);
                                }
                                String archKey = room + "-arch";
                                try (Jedis jedis = jedisPool.getResource()) {
                                    jedis.lpush(archKey, distributingMessage);
                                }
                                try (Jedis jedis = jedisPool.getResource()) {
                                    jedis.expire(archKey, 60 * 60 * 24);
                                }
                            }
                    }
                } else {
                    if (chatService.clientAbsent(value)) {
                        sendToClient(
                                objectMapper.writeValueAsString(
                                        new Message(value,"","") ) );
                        clientName = value;
                        authorized = true;
                        expectedKey = "message";
                    }
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

    public void sendToClient(String data) {
        try {
            session.getRemote().sendString(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
