package controllers;

import java.util.HashMap;
import java.util.Map;

import rev.annotation.controller.Auth;
import rev.annotation.controller.Controller;
import rev.annotation.controller.Role;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;

@Controller
public class Test8 {

    private static int compteur = 0;

    private Map<String, Object> reponse(String route) {
        compteur++;
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("route", route);
        resultat.put("invocations", compteur);
        return resultat;
    }

    @Role({"admin", "RH"})
    @WebApi
    @UrlMapping(value = "/roles/rh-admin", method = "GET")
    public Map<String, Object> rhAdmin() {
        return reponse("rh-admin");
    }

    @Role("admin")
    @WebApi
    @UrlMapping(value = "/roles/admin-seul", method = "GET")
    public Map<String, Object> adminSeul() {
        return reponse("admin-seul");
    }

    @Role({"admin", "RH"})
    @UrlMapping(value = "/roles/page", method = "GET")
    public String page() {
        compteur++;
        return "Page réservée aux admin et RH";
    }

    @Auth
    @WebApi
    @UrlMapping(value = "/roles/invocations", method = "GET")
    public Map<String, Object> invocations() {
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("invocations", compteur);
        return resultat;
    }
}