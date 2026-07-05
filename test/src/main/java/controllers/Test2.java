package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
@Controller
public class Test2 {

    @UrlMapping(value = "/Test2/accueil", method = "POST")
    public String accueil() {
        return "Bienvenue sur la page d'accueil";
    }
}
