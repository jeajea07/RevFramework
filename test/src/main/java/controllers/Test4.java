package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;
import rev.utils.ModelAndView;
import entities.Employe;

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

    @UrlMapping(value = "/Test4/save", method = "POST")
    public ModelAndView save(Employe employe) {
        ModelAndView mv = new ModelAndView("resultat");
        mv.addObject("employe", employe);
        return mv;
    }
}