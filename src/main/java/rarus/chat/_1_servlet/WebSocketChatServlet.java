package rarus.chat._1_servlet;

import rarus.chat._2_webSocket.WebSocketChatInstance;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import rarus.chat.Config;
import rarus.chat.Main;

import javax.servlet.annotation.WebServlet;


@WebServlet(name = "WebSocketChatServlet", urlPatterns = {"/chat"})
public class WebSocketChatServlet extends WebSocketServlet {

    public static final String PATH = "/chat";
    private static final int LOGOUT_TIME = 10 * 60 * 1000;
    private final String room;

    public WebSocketChatServlet() {
        Config config = (Config) Main.context.get(Config.class);
        this.room = config.getProperty("room");
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(LOGOUT_TIME);
        factory.setCreator((req, resp) -> new WebSocketChatInstance(room));
    }

}
