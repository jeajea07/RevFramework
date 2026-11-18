package rev.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.List;
import java.util.Map;

import rev.annotation.controller.Controller;
import rev.utils.Mapping;
import rev.utils.UrlMethode;
import rev.utils.Utilitaire;

@WebListener
public class RevInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext context = sce.getServletContext();
            String packageName = context.getInitParameter("controllerPackage");
            String authAttribute = context.getInitParameter("authAttribute");

            Utilitaire utils = new Utilitaire();
            List<String> classList = utils.getClassLists(packageName);
            List<String> annotedClasses = utils.getAnnotedClassList(Controller.class, "classe", classList);
            Map<UrlMethode, Mapping> urlMappings = utils.getUrlMappings(annotedClasses);

            context.setAttribute("annotedClasses", annotedClasses);
            context.setAttribute("urlMappings", urlMappings);
            context.setAttribute("authAttribute", authAttribute);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation du framework rev", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}