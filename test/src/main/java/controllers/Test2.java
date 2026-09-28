package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;
@Controller
public class Test2 {
    @UrlMapping(value = "/Test2/accueil", method = "GET")
    public String accueil() {
        return "Bienvenue sur la page d'accueil";
    }
}
