package rev.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import rev.annotation.controller.Controller;
import rev.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotedClasses;
    @Override
    public void init() throws ServletException {

        try {
            String packageName = this.getInitParameter("controllerPackage");

            Utilitaire utils = new Utilitaire();
            List<String> classList = utils.getClassLists(packageName);
            this.annotedClasses = utils.getAnnotedClassList(Controller.class, "classe", classList);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String method = req.getMethod();
        String url    = req.getRequestURI();
        String query  = req.getQueryString();

        out.println("<p>Tongasoa ato amin'ny <b>" + method + "</b></p>");
        out.println("<p><b>URI   :</b> " + url   + "</p>");
        out.println("<p><b>Query :</b> " + query + "</p>");
        
        out.println("<p><b>Controllers détectés :</b></p>");
        out.println("<ul>");
        for (String className : annotedClasses) {
            out.println("<li>" + className + "</li>");
        }
        out.println("</ul>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        processRequest(req, res);
    }
}