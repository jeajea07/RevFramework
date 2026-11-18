package controllers;

import java.util.HashMap;
import java.util.Map;

import rev.annotation.controller.Auth;
import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;

@Controller
@Auth(role = "admin")
public class Test7 {

    private static int compteur = 0;

    private Map<String, Object> reponse(String route) {
        compteur++;
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("route", route);
        resultat.put("invocations", compteur);
        return resultat;
    }

    @WebApi
    @UrlMapping(value = "/secure/dashboard", method = "GET")
    public Map<String, Object> dashboard() {
        return reponse("dashboard");
    }

    @Auth
    @WebApi
    @UrlMapping(value = "/secure/profile", method = "GET")
    public Map<String, Object> profile() {
        return reponse("profile");
    }

    @Auth(role = "manager")
    @WebApi
    @UrlMapping(value = "/secure/reports", method = "GET")
    public Map<String, Object> reports() {
        return reponse("reports");
    }

    @UrlMapping(value = "/secure/page", method = "GET")
    public String page() {
        compteur++;
        return "Page réservée aux admins";
    }

    @Auth
    @WebApi
    @UrlMapping(value = "/secure/invocations", method = "GET")
    public Map<String, Object> invocations() {
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("invocations", compteur);
        return resultat;
    }
}