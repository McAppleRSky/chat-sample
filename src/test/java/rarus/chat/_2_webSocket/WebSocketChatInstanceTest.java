package rarus.chat._2_webSocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import rarus.chat.main.Main;
import rarus.chat.model.Message;

import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.*;

class WebSocketChatInstanceTest {

    @Test
    void serialKeyTDDTest(){
        DateTimeFormatter dateTimeFormatter = (DateTimeFormatter)Main.context.get(DateTimeFormatter.class);
        String name="testName", dateTime="yyyy-MM-dd HH:mm:ss", text="testText", actual=null, expected;
        Message message = new Message(name, dateTime, text), messageRecovered = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            actual = objectMapper.writeValueAsString(message);
            messageRecovered = objectMapper.readValue(actual, Message.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        expected="{\"name\":\"testName\",\"text\":\"testText\",\"date_time\":\"yyyy-MM-dd HH:mm:ss\"}";
        assertEquals(expected, actual);
        assertNotNull(messageRecovered);
        assertEquals(name, messageRecovered.getName());
        assertEquals(dateTime, messageRecovered.getDateTime());
        assertEquals(text, messageRecovered.getText());
    }

}