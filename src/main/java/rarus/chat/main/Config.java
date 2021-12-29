package rarus.chat.main;

import java.util.HashMap;
import java.util.Map;

//@SuppressWarnings("unchecked")
public class Config {

    private final Map<String, String> properties = new HashMap<>();

    public Config(String property, String value) {
        setProperty(property, value);
    }

    public String getProperty(String property) {
        return properties.get(property);
    }

    public void setProperty(String property, String value) {
        properties.put(property, value);
    }

}
