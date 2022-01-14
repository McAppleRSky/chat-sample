package rarus.chat;

import rarus.chat._1_servlet.WebSocketChatServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import rarus.chat._3_service.ChatService;
import rarus.chat._3_service.ChatServiceImpl;
import redis.clients.jedis.JedisPool;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;



public class Main extends MainConfiguration {

    public static final Map context = new HashMap();

    static {
        {
            String publicHtml = Main.class.getClassLoader().getResource("public_html").getPath();
            publicHtml = publicHtml.substring(1, publicHtml.length()); // windows path fix
            context.put("public_html", publicHtml);
        }
        context.put(ConfigHide.class, configureHide());
        {
            context.put(Config.class, configureChat());
            Config config = (Config) context.get(Config.class);
            config.setProperty("room", randomAlphabetic(4));
        }
        context.put(JedisPool.class, configureJedis());
        context.put(DateTimeFormatter.class, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        context.put(ChatService.class, new ChatServiceImpl());
    }

    public static void main(String[] args) throws Exception {

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        servletContextHandler.addServlet(new ServletHolder( new WebSocketChatServlet() ), WebSocketChatServlet.PATH);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);

        resource_handler.setResourceBase((String)context.get("public_html"));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, servletContextHandler});

        Config config = (Config)context.get(Config.class);
        int port = Integer.parseInt(config.getProperty("server-port"));
        Server server = new Server(port);

        server.setHandler(handlers);
        System.out.println("Server started");
        server.start();
        server.join();
    }

}
