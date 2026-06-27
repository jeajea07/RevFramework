package controllers;
import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;

@Controller
public class Test1 {

    @UrlMapping("/Test1/andrana")
    public String andrana() {
        return "Bienvenue sur la page d'accueil";
    }
}
