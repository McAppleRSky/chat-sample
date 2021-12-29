package rarus.chat.main;

import rarus.chat._1_servlet.WebSocketChatServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.IOException;

public class Main {

    public static String PUBLIC_HTML_RESOURCE = Main.class.getClassLoader().getResource("public_html").getPath();
//    public static String HTML_RESOURCE = Main.class.getClassLoader().getResource("html").getPath();
//    public static Configuration freemarkerConfiguration = new Configuration(freemarker.template.Configuration.VERSION_2_3_27);
    static {
        PUBLIC_HTML_RESOURCE = PUBLIC_HTML_RESOURCE.substring(1, PUBLIC_HTML_RESOURCE.length()); // windows path fix
/*
        try {
            freemarkerConfiguration
                    .setDirectoryForTemplateLoading(
                            new File(
                                    Main.class
                                            .getClassLoader()
                                            .getResource("template")
                                            .getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder( new WebSocketChatServlet() ), WebSocketChatServlet.PATH);
//        context.addServlet(new ServletHolder( new LoginServlet() ), LoginServlet.PATH);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);

        resource_handler.setResourceBase(PUBLIC_HTML_RESOURCE);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);
        System.out.println("Server started");
        server.start();
        server.join();
    }

}
