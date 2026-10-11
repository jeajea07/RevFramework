package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;
import rev.utils.ModelAndView;

@Controller
public class Test4 {

    @UrlMapping(value = "/Test4/ajouter", method = "POST")
    public ModelAndView ajouter(String nom, int quantite) {
        ModelAndView mv = new ModelAndView("resultat");
        mv.addObject("nom", nom);
        mv.addObject("quantite", quantite);
        return mv;
    }

    @UrlMapping(value = "/Test4/formulaire", method = "GET")
    public ModelAndView formulaire() {
        return new ModelAndView("form");
    }
}