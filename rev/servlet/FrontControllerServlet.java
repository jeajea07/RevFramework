package rev.servlet;

import jakarta.servlet.DispatcherType;
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
import rev.utils.ModelAndView;
import rev.utils.UrlMethode;
import rev.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotedClasses;
    private Map<UrlMethode, Mapping> urlMappings;
    private String prefix;
    private String suffix;

    @Override
    public void init() throws ServletException {

        try {
            String packageName = this.getInitParameter("controllerPackage");
            this.prefix = this.getInitParameter("prefix");
            this.suffix = this.getInitParameter("suffix");

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

        UrlMethode key = new UrlMethode(calledUrl, method);
        Mapping found = urlMappings.get(key);

        if (found != null) {
            try {
                Class<?> controllerClass = found.getControllerClass();
                Object instance = controllerClass.getDeclaredConstructor().newInstance();

                Object result = found.getMethod().invoke(instance);

                if (result instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) result;

                    for (Map.Entry<String, Object> entry : mv.getDonnees().entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }

                    String cheminVue = prefix + mv.getVue() + suffix;

                    RequestDispatcher dispatcher = req.getRequestDispatcher(cheminVue);
                    dispatcher.forward(req, res);
                    return; 

                } else {
                    out.println("<p>Tongasoa ato amin'ny <b>" + method + "</b></p>");
                    out.println("<p><b>URI   :</b> " + url   + "</p>");
                    out.println("<p><b>Query :</b> " + query + "</p>");
                    out.println("<hr>");
                    out.println("<p><b>Résultat de l'exécution :</b></p>");
                    out.println("<p>" + result + "</p>");
                }

            } catch (Exception e) {
                out.println("<p style=\"color:red;\"><b>Erreur lors de l'invocation :</b> " + e.getMessage() + "</p>");
                e.printStackTrace();
            }

        } else {
            out.println("<p>Tongasoa ato amin'ny <b>" + method + "</b></p>");
            out.println("<p><b>URI   :</b> " + url   + "</p>");
            out.println("<p><b>Query :</b> " + query + "</p>");
            out.println("<p><b>Aucune méthode ne correspond à cette url (avec cette méthode HTTP).</b></p>");
            out.println("<p><b>Voici les controllers détectés et leurs urlMapping :</b></p>");

            for (String className : annotedClasses) {
                out.println("<p><b>Controller :</b> " + className + "</p>");
                out.println("<ul>");
                boolean hasMapping = false;
                for (Map.Entry<UrlMethode, Mapping> entry : urlMappings.entrySet()) {
                    Mapping mapping = entry.getValue();
                    if (mapping.getControllerClass().getName().equals(className)) {
                        UrlMethode um = entry.getKey();
                        out.println("<li><b>" + um.getMethode() + " " + um.getUrl()
                                + "</b> -> " + mapping.getMethod().getName() + "()</li>");
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

        if (req.getDispatcherType() == DispatcherType.FORWARD) {
            String jspPath = req.getServletPath();
            req.setAttribute("org.apache.catalina.jsp_file", jspPath);
            getServletContext().getNamedDispatcher("jsp").forward(req, res);
            return;
        }

        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        if (req.getDispatcherType() == DispatcherType.FORWARD) {
            String jspPath = req.getServletPath();
            req.setAttribute("org.apache.catalina.jsp_file", jspPath);
            getServletContext().getNamedDispatcher("jsp").forward(req, res);
            return;
        }

        processRequest(req, res);
    }
}