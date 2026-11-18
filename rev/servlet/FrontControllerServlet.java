package rev.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import rev.annotation.controller.Auth;
import rev.annotation.controller.WebApi;
import rev.utils.FichierUpload;
import rev.utils.Mapping;
import rev.utils.ModelAndView;
import rev.utils.ResponseHelper;
import rev.utils.RevSession;
import rev.utils.UrlMethode;

@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 20 * 1024 * 1024
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

    private String toJson(Object objet) throws Exception {
        return new ObjectMapper().writeValueAsString(objet);
    }

    private Auth trouverAuth(Method methode, Class<?> classe) {
        Auth auth = methode.getAnnotation(Auth.class);
        if (auth == null) {
            auth = classe.getAnnotation(Auth.class);
        }
        return auth;
    }

    private boolean possedeRole(Object roles, String roleRequis) {
        if (roles instanceof String) {
            return roles.equals(roleRequis);
        }
        if (roles instanceof Collection) {
            return ((Collection<?>) roles).contains(roleRequis);
        }
        return false;
    }

    private boolean checkAuthorization(Method methode, Class<?> classe, HttpServletRequest req,
                                       HttpServletResponse res, boolean api) throws IOException {
        Auth auth = trouverAuth(methode, classe);
        if (auth == null) {
            return true;
        }

        String nomAttribut = (String) getServletContext().getAttribute("authAttribute");
        HttpSession session = req.getSession(false);
        Object roles = null;
        if (session != null && nomAttribut != null) {
            roles = session.getAttribute(nomAttribut);
        }

        if (roles == null) {
            ResponseHelper.handleUnauthorized(res, api);
            return false;
        }

        String roleRequis = auth.role();
        if (roleRequis.isEmpty()) {
            return true;
        }

        if (!possedeRole(roles, roleRequis)) {
            ResponseHelper.handleForbidden(res, api);
            return false;
        }
        return true;
    }

    private Object[] resolveArgsFromRequest(Method method, HttpServletRequest req) throws Exception {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            Class<?> type = p.getType();

            if (ApplicationContext.class.isAssignableFrom(type)) {
                args[i] = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            } else if (type == FichierUpload.class) {
                FichierUpload fichier = lireFichier(p.getName(), req);
                if (fichier == null) {
                    throw new IllegalArgumentException("Fichier manquant : " + p.getName());
                }
                args[i] = fichier;
            } else if (type == RevSession.class) {
                args[i] = new RevSession(req.getSession());
            } else if (!isSimple(type)) {
                args[i] = bindObject(type, req);
            } else {
                String valeur = req.getParameter(p.getName());
                if (valeur == null) {
                    throw new IllegalArgumentException("Paramètre manquant : " + p.getName());
                }
                args[i] = convertir(valeur, type, p.getName());
            }
        }
        return args;
    }

    private boolean isSimple(Class<?> t) {
        return t.isPrimitive() || t == String.class
            || Number.class.isAssignableFrom(t) || t == Boolean.class;
    }

    private boolean isMultipart(HttpServletRequest req) {
        String contentType = req.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    private FichierUpload lireFichier(String nomParam, HttpServletRequest req) throws Exception {
        if (!isMultipart(req)) {
            return null;
        }

        Part part = req.getPart(nomParam);
        if (part == null || part.getSize() == 0) {
            return null;
        }

        String soumis = part.getSubmittedFileName();
        if (soumis == null || soumis.isBlank()) {
            return null;
        }

        String original = Paths.get(soumis.replace('\\', '/')).getFileName().toString();
        String nom = original;
        String extension = "";
        int point = original.lastIndexOf('.');
        if (point > 0) {
            nom = original.substring(0, point);
            extension = original.substring(point + 1);
        }

        byte[] contenu;
        try (InputStream in = part.getInputStream()) {
            contenu = in.readAllBytes();
        }
        return new FichierUpload(nom, extension, contenu);
    }

    private Object bindObject(Class<?> type, HttpServletRequest req) throws Exception {
        Object objet = type.getDeclaredConstructor().newInstance();
        boolean auMoinsUn = false;

        for (Field champ : type.getDeclaredFields()) {
            if (Modifier.isStatic(champ.getModifiers())) {
                continue;
            }

            if (champ.getType() == FichierUpload.class) {
                FichierUpload fichier = lireFichier(champ.getName(), req);
                if (fichier != null) {
                    champ.setAccessible(true);
                    champ.set(objet, fichier);
                    auMoinsUn = true;
                }
                continue;
            }

            String valeur = req.getParameter(champ.getName());
            if (valeur == null) {
                continue;
            }

            champ.setAccessible(true);
            champ.set(objet, convertir(valeur, champ.getType(), champ.getName()));
            auMoinsUn = true;
        }

        if (!auMoinsUn) {
            throw new IllegalArgumentException("Aucun paramètre ne correspond à " + type.getSimpleName());
        }
        return objet;
    }

    private Object convertir(String valeur, Class<?> type, String nomParam) {
        try {
            if (type == String.class) return valeur;
            if (type == int.class || type == Integer.class) return Integer.parseInt(valeur);
            if (type == long.class || type == Long.class) return Long.parseLong(valeur);
            if (type == double.class || type == Double.class) return Double.parseDouble(valeur);
            if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(valeur);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Paramètre '" + nomParam + "' invalide : " + valeur);
        }
        throw new IllegalArgumentException("Type non supporté : " + type.getName());
    }

    private void traiterApi(Mapping mapping, HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        try {
            Method methode = mapping.getMethod();
            Object instance = mapping.getControllerClass().getDeclaredConstructor().newInstance();
            Object[] args = resolveArgsFromRequest(methode, req);
            Object resultat = methode.invoke(instance, args);

            res.setContentType("application/json;charset=UTF-8");
            out.print(toJson(resultat));

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json;charset=UTF-8");
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void traiterPage(Mapping mapping, HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        PrintWriter out = res.getWriter();
        try {
            Method methode = mapping.getMethod();
            Object instance = mapping.getControllerClass().getDeclaredConstructor().newInstance();
            Object[] args = resolveArgsFromRequest(methode, req);
            Object resultat = methode.invoke(instance, args);

            if (resultat instanceof ModelAndView) {
                afficherVue((ModelAndView) resultat, req, res);
            } else {
                out.println("<p>" + resultat + "</p>");
            }

        } catch (Exception e) {
            out.println("<p style=\"color:red;\">Erreur : " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }

    private void afficherVue(ModelAndView mv, HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        for (Map.Entry<String, Object> entry : mv.getDonnees().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }

        String prefix = getServletContext().getInitParameter("prefix");
        String suffix = getServletContext().getInitParameter("suffix");
        String cheminVue = prefix + mv.getVue() + suffix;

        RequestDispatcher dispatcher = req.getRequestDispatcher(cheminVue);
        dispatcher.forward(req, res);
    }

    private void afficherUrlInconnue(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        Map<UrlMethode, Mapping> urlMappings = getUrlMappings();

        out.println("<p><b>Aucune méthode ne correspond à cette url.</b></p>");
        out.println("<p>URI : " + req.getRequestURI() + "</p>");
        out.println("<p>Controllers détectés :</p>");

        for (String className : getAnnotedClasses()) {
            out.println("<p><b>" + className + "</b></p>");
            out.println("<ul>");
            for (Map.Entry<UrlMethode, Mapping> entry : urlMappings.entrySet()) {
                if (entry.getValue().getControllerClass().getName().equals(className)) {
                    out.println("<li>" + entry.getKey() + " -> "
                            + entry.getValue().getMethod().getName() + "()</li>");
                }
            }
            out.println("</ul>");
        }
    }

    void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String calledUrl = req.getRequestURI().substring(req.getContextPath().length());
        UrlMethode cle = new UrlMethode(calledUrl, req.getMethod());
        Mapping mapping = getUrlMappings().get(cle);

        if (mapping == null) {
            afficherUrlInconnue(req, res);
            return;
        }

        Method methode = mapping.getMethod();
        boolean api = methode.isAnnotationPresent(WebApi.class);

        if (!checkAuthorization(methode, mapping.getControllerClass(), req, res, api)) {
            return;
        }

        if (api) {
            traiterApi(mapping, req, res);
        } else {
            traiterPage(mapping, req, res);
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