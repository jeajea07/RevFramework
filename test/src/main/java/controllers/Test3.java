package controllers;

import org.springframework.context.ApplicationContext;
import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;
import services.FruitService;
import entities.Fruit;

import java.util.List;

@Controller
public class Test3 {

    @WebApi
    @UrlMapping(value = "/Test3/fruits", method = "GET")
    public List<Fruit> fruits(ApplicationContext ctx) {
        return ctx.getBean(FruitService.class).getFruits();
    }

    @WebApi
    @UrlMapping(value = "/Test3/accueil", method = "GET")
    public String accueil() {
        return "Bienvenue sur la page d'accueil";
    }
}