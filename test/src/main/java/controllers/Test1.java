package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;

@Controller
public class Test1 {

    @UrlMapping(value = "/Test1/andrana", method = "GET")
    public String andrana() {
        return "Bienvenue sur la page d'accueil";
    }
}