package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.utils.ModelAndView;
import rev.utils.RevSession;

@Controller
public class Test6 {

    @UrlMapping(value = "/Test6/accueil/afficher", method = "GET")
    public ModelAndView afficher(RevSession session) {
        ModelAndView mv = new ModelAndView("accueil");
        mv.addObject("message", "Contenu actuel de la session");
        return mv;
    }

    @UrlMapping(value = "/Test6/accueil/ajouter", method = "POST")
    public ModelAndView ajouter(String cle, String valeur, RevSession session) {
        session.add(cle, valeur);
        ModelAndView mv = new ModelAndView("accueil");
        mv.addObject("message", "Ajouté : " + cle + " = " + valeur);
        return mv;
    }

    @UrlMapping(value = "/Test6/accueil/supprimer", method = "POST")
    public ModelAndView supprimer(String cle, RevSession session) {
        session.remove(cle);
        ModelAndView mv = new ModelAndView("accueil");
        mv.addObject("message", "Supprimé : " + cle);
        return mv;
    }
}