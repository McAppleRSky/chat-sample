package rarus.chat._1_servlet;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static rarus.chat.main.Main.freemarkerConfiguration;

public class LoginServlet extends HttpServlet implements Servletable{

    public static final String PATH = "/login";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String login = req.getParameter("login");
//        req.getSession().setAttribute("login", login);
        Map<String, String> data = new HashMap<>();
        data.put("login", login);
        Template template // = null
                ;
        try ( PrintWriter writer = res.getWriter() ) {
            template = freemarkerConfiguration.getTemplate("chat.ftl");
            res.setContentType(COMMON_CONTENT_TYPE);
            res.setStatus(HttpServletResponse.SC_OK);
            template.process(data, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

}
