package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.utils.FichierUpload;
import rev.utils.ModelAndView;

@Controller
public class Test5 {

    @UrlMapping(value = "/Test5/formulaire", method = "GET")
    public ModelAndView formulaire() {
        return new ModelAndView("upload");
    }

    @UrlMapping(value = "/Test5/envoyer", method = "POST")
    public ModelAndView envoyer(String description, FichierUpload fichier) throws Exception {
        String dossier = "/home/jeannie/Documents/IT-University/L2/S6/RevFramework/test/src/main/java/uploads";
        fichier.sauvegarder(dossier);

        ModelAndView mv = new ModelAndView("resultat-upload");
        mv.addObject("description", description);
        mv.addObject("fichier", fichier);
        return mv;
    }
}