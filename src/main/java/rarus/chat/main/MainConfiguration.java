package rarus.chat.main;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

@SuppressWarnings("deprecation")
public class MainConfiguration {

    protected static ConfigHide configureHide(){
        ConfigHide result = null;
        Properties properties = new Properties();
        try (InputStream input = MainConfiguration.class.getClassLoader().getResourceAsStream("chat-hide.properties")) {
            properties.load(input);
            result = new ConfigHide(
                    properties.getProperty("redis-pass")
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    protected static Config configureChat() {
        Config result = null;
        Properties properties = new Properties();
        try (InputStream input = MainConfiguration.class.getClassLoader().getResourceAsStream("chat.properties")) {
            properties.load(input);
            result = new Config("server-port", properties.getProperty("server-port"));
            result.setProperty("redis-host", properties.getProperty("redis-host"));
            result.setProperty("redis-port", properties.getProperty("redis-port"));
            result.setProperty("redis-timeout", properties.getProperty("redis-timeout"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    protected static JedisPool configureJedis() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(128);
        jedisPoolConfig.setMaxIdle(128);
        jedisPoolConfig.setMinIdle(16);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        jedisPoolConfig.setNumTestsPerEvictionRun(3);
        jedisPoolConfig.setBlockWhenExhausted(true);

        Config config = (Config) Main.context.get(Config.class);
        String host = config.getProperty("redis-host");
        int port = Integer.parseInt(config.getProperty("redis-port"));
        int timeout = Integer.parseInt(config.getProperty("redis-timeout"));
        ConfigHide configHide = (ConfigHide) Main.context.get(ConfigHide.class);
        String password = configHide.getRedisPassword();
        return new JedisPool(jedisPoolConfig, host, port, timeout, password);
    }

}
