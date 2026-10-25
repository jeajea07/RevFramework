package rev.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import rev.utils.FichierUpload;
import rev.utils.Mapping;
import rev.utils.ModelAndView;
import rev.utils.RevSession;
import rev.utils.UrlMethode;
import rev.annotation.controller.WebApi;

@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,      // 10 Mo par fichier
    maxRequestSize = 20 * 1024 * 1024    // 20 Mo par requête
)
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
        return mapper.writeValueAsString(monObjet);
    }

    private Object[] resolveArgsFromRequest(Method method, HttpServletRequest req) throws Exception {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            Class<?> type = p.getType();

            if (ApplicationContext.class.isAssignableFrom(type)) {
                args[i] = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
                continue;
            }

            // Upload : doit être testé AVANT le binding d'objet
            if (type == FichierUpload.class) {
                FichierUpload f = lireFichier(p.getName(), req);
                if (f == null) {
                    throw new IllegalArgumentException(
                        "Fichier manquant : '" + p.getName() + "' attendu par " + method.getName());
                }
                args[i] = f;
                continue;
            }

            // Session : HttpSession -> RevSession (doit aussi être avant le binding d'objet)
            if (type == RevSession.class) {
                RevSession rs = new RevSession();
                HttpSession http = req.getSession();
                Enumeration<String> noms = http.getAttributeNames();
                while (noms.hasMoreElements()) {
                    String nom = noms.nextElement();
                    rs.add(nom, http.getAttribute(nom));
                }
                args[i] = rs;
                continue;
            }

            // Paramètre de type objet -> on le construit depuis la requête
            if (!isSimple(type)) {
                args[i] = bindObject(type, req);
                continue;
            }

            String nomParam = p.getName();
            String valeur = req.getParameter(nomParam);
            if (valeur == null) {
                throw new IllegalArgumentException(
                    "Paramètre manquant : '" + nomParam + "' attendu par " + method.getName());
            }
            args[i] = convertir(valeur, type, nomParam);
        }
        return args;
    }

    // RevSession -> HttpSession : on écrase la vraie session avec le contenu de RevSession
    private void syncSession(Object[] args, HttpServletRequest req) {
        for (Object arg : args) {
            if (arg instanceof RevSession) {
                HttpSession http = req.getSession();

                // 1. on vide la vraie session
                List<String> aSupprimer = Collections.list(http.getAttributeNames());
                for (String nom : aSupprimer) {
                    http.removeAttribute(nom);
                }

                // 2. on la remplit avec le contenu de RevSession
                for (Map.Entry<String, Object> e : ((RevSession) arg).getDonnees().entrySet()) {
                    http.setAttribute(e.getKey(), e.getValue());
                }
            }
        }
    }

    private boolean isSimple(Class<?> t) {
        return t.isPrimitive() || t == String.class
            || Number.class.isAssignableFrom(t) || t == Boolean.class;
    }

    private boolean isMultipart(HttpServletRequest req) {
        String ct = req.getContentType();
        return ct != null && ct.toLowerCase().startsWith("multipart/");
    }

    private FichierUpload lireFichier(String nomParam, HttpServletRequest req) throws Exception {
        if (!isMultipart(req)) return null;

        Part part = req.getPart(nomParam);
        if (part == null || part.getSize() == 0) return null;

        String soumis = part.getSubmittedFileName();
        if (soumis == null || soumis.isBlank()) return null;

        String original = Paths.get(soumis.replace('\\', '/')).getFileName().toString();

        String nom = original;
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot > 0) {
            nom = original.substring(0, dot);
            extension = original.substring(dot + 1);
        }

        byte[] contenu;
        try (InputStream in = part.getInputStream()) {
            contenu = in.readAllBytes();
        }
        return new FichierUpload(nom, extension, contenu);
    }

    private Object bindObject(Class<?> type, HttpServletRequest req) throws Exception {
        Object obj = type.getDeclaredConstructor().newInstance();
        boolean auMoinsUn = false;

        for (Field f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;

            // Champ de type FichierUpload dans l'objet
            if (f.getType() == FichierUpload.class) {
                FichierUpload fichier = lireFichier(f.getName(), req);
                if (fichier != null) {
                    f.setAccessible(true);
                    f.set(obj, fichier);
                    auMoinsUn = true;
                }
                continue;
            }

            String valeur = req.getParameter(f.getName());
            if (valeur == null) continue;

            Object convertie = convertir(valeur, f.getType(), f.getName());

            f.setAccessible(true);
            f.set(obj, convertie);
            auMoinsUn = true;
        }

        if (!auMoinsUn) {
            throw new IllegalArgumentException(
                "Aucun paramètre de la requête ne correspond aux attributs de " + type.getSimpleName());
        }
        return obj;
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
            if (methode.isAnnotationPresent(WebApi.class)) {
                try {
                    Class<?> controllerClass = found.getControllerClass();
                    Object instance = controllerClass.getDeclaredConstructor().newInstance();
                    Object[] args = resolveArgsFromRequest(methode, req);
                    Object resultat = methode.invoke(instance, args);

                    syncSession(args, req);   // RevSession -> HttpSession

                    String body = toJson(resultat);
                    res.setContentType("application/json;charset=UTF-8");
                    out.print(body);

                } catch (Exception e) {
                    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    res.setContentType("application/json;charset=UTF-8");
                    out.print("{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
            else {
                try {
                    Class<?> controllerClass = found.getControllerClass();
                    Object instance = controllerClass.getDeclaredConstructor().newInstance();

                    Method methodToInvoke = found.getMethod();
                    Object[] args = resolveArgsFromRequest(methodToInvoke, req);
                    Object result = methodToInvoke.invoke(instance, args);

                    syncSession(args, req);   // RevSession -> HttpSession, AVANT le forward

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