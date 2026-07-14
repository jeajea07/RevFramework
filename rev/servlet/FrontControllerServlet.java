package rev.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import rev.utils.Mapping;
import rev.utils.ModelAndView;
import rev.utils.UrlMethode;

public class FrontControllerServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    private Map<UrlMethode, Mapping> getUrlMappings() {
        return (Map<UrlMethode, Mapping>) getServletContext().getAttribute("urlMappings");
    }

    @SuppressWarnings("unchecked")
    private List<String> getAnnotedClasses() {
        return (List<String>) getServletContext().getAttribute("annotedClasses");
    }

    void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String method = req.getMethod();
        String url    = req.getRequestURI();
        String query  = req.getQueryString();

        String contextPath = req.getContextPath();
        String calledUrl = url.substring(contextPath.length());

        Map<UrlMethode, Mapping> urlMappings = getUrlMappings();
        List<String> annotedClasses = getAnnotedClasses();

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

                    String prefix = getServletContext().getInitParameter("prefix");
                    String suffix = getServletContext().getInitParameter("suffix");
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
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        processRequest(req, res);
    }
}