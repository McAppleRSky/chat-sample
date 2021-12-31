package rarus.chat._3_service;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceImplTest {

    @Test
    void arrayTest(){
        Set set = Collections.newSetFromMap(new ConcurrentHashMap<>());
        String[] strings = {"uno", "duo", "tre"};
        for (String s : strings) {
            set.add(s);
        }
        Object[] actual = set.toArray();
        HashSet<String> setExpected = new HashSet<>(Arrays.asList(strings));
        assertNotEquals(strings, actual);
        assertEquals(strings.length, setExpected.size());
        for (Object o : actual) {
            setExpected.remove(o);
        }
        assertTrue(setExpected.isEmpty());
    }

}