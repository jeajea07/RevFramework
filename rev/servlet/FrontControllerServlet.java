package rev.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import rev.utils.Mapping;
import rev.utils.ModelAndView;
import rev.utils.UrlMethode;
import rev.annotation.controller.WebApi;

public class FrontControllerServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    private Map<UrlMethode, Mapping> getUrlMappings() {
        return (Map<UrlMethode, Mapping>) getServletContext().getAttribute("urlMappings");
    }

    @SuppressWarnings("unchecked")
    private List<String> getAnnotedClasses() {
        return (List<String>) getServletContext().getAttribute("annotedClasses");
    }

    private String toJson(Object monObjet) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(monObjet);
        return json;    
    }

    private Object[] resolveArgsFromRequest(Method method, HttpServletRequest req) throws Exception {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            Class<?> type = p.getType();

            // cas spécial déjà existant : injection du contexte Spring
            if (ApplicationContext.class.isAssignableFrom(type)) {
                args[i] = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
                continue;
            }

            String nomParam = p.getName();
            String valeur = req.getParameter(nomParam);

            // vérification : le paramètre attendu doit exister dans la requête
            if (valeur == null) {
                throw new IllegalArgumentException(
                    "Paramètre manquant : '" + nomParam + "' attendu par " + method.getName());
            }

            // conversion simple selon le type attendu, sans construire d'objet
            args[i] = convertir(valeur, type, nomParam);
        }
        return args;
    }

    private Object convertir(String valeur, Class<?> type, String nomParam) {
        try {
            if (type == String.class) return valeur;
            if (type == int.class || type == Integer.class) return Integer.parseInt(valeur);
            if (type == long.class || type == Long.class) return Long.parseLong(valeur);
            if (type == double.class || type == Double.class) return Double.parseDouble(valeur);
            if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(valeur);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Paramètre '" + nomParam + "' = '" + valeur + "' incompatible avec " + type.getSimpleName());
        }
        throw new IllegalArgumentException("Type non supporté pour le moment : " + type.getName());
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
            Method methode = found.getMethod();
            if(methode.isAnnotationPresent(WebApi.class)){
                try {
                    Class<?> controllerClass = found.getControllerClass();
                    Object instance = controllerClass.getDeclaredConstructor().newInstance();
                    Object[] args = resolveArgsFromRequest(methode, req);
                    Object resultat = methode.invoke(instance, args);
                   
                    String body = toJson(resultat);
                    res.setContentType("application/json;charset=UTF-8");
                    out.print(body);

                } catch (Exception e) {
                    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    res.setContentType("application/json;charset=UTF-8");
                    out.print("{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
            else{
                try {
                    Class<?> controllerClass = found.getControllerClass();
                    Object instance = controllerClass.getDeclaredConstructor().newInstance();

                    Method methodToInvoke = found.getMethod();
                    Object[] args = resolveArgsFromRequest(methodToInvoke, req);
                    Object result = methodToInvoke.invoke(instance, args);

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