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
import java.util.Map;

import rev.annotation.controller.Controller;
import rev.utils.Mapping;
import rev.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotedClasses;
    private Map<String, Mapping> urlMappings;

    @Override
    public void init() throws ServletException {

        try {
            String packageName = this.getInitParameter("controllerPackage");

            Utilitaire utils = new Utilitaire();
            List<String> classList = utils.getClassLists(packageName);
            this.annotedClasses = utils.getAnnotedClassList(Controller.class, "classe", classList);
            this.urlMappings = utils.getUrlMappings(this.annotedClasses);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String method = req.getMethod();
        String url    = req.getRequestURI();
        String query  = req.getQueryString();

        String contextPath = req.getContextPath();
        String calledUrl = url.substring(contextPath.length());

        out.println("<p>Tongasoa ato amin'ny <b>" + method + "</b></p>");
        out.println("<p><b>URI   :</b> " + url   + "</p>");
        out.println("<p><b>Query :</b> " + query + "</p>");

        Mapping found = urlMappings.get(calledUrl);

        if (found != null) {
            out.println("<p><b>Controller trouvé :</b></p>");
            out.println("<ul>");
            out.println("<li><b>URL       :</b> " + calledUrl + "</li>");
            out.println("<li><b>Controller:</b> " + found.getControllerClass().getName() + "</li>");
            out.println("<li><b>Méthode   :</b> " + found.getMethod().getName() + "</li>");
            out.println("</ul>");
        } else {
            out.println("<p><b>Aucune méthode ne correspond à cette url.</b></p>");
            out.println("<p><b>Voici les controllers détectés et leurs urlMapping :</b></p>");

            for (String className : annotedClasses) {
                out.println("<p><b>Controller :</b> " + className + "</p>");
                out.println("<ul>");
                boolean hasMapping = false;
                for (Map.Entry<String, Mapping> entry : urlMappings.entrySet()) {
                    Mapping mapping = entry.getValue();
                    if (mapping.getControllerClass().getName().equals(className)) {
                        out.println("<li><b>" + entry.getKey() + "</b> -> " + mapping.getMethod().getName() + "()</li>");
                        hasMapping = true;
                    }
                }
                if (!hasMapping) {
                    out.println("<li><i>aucune méthode urlMapping</i></li>");
                }
                out.println("</ul>");
            }
        }
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