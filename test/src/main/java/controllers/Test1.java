package controllers;

import java.util.ArrayList;
import java.util.List;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.utils.ModelAndView;

@Controller
public class Test1 {

    @UrlMapping(value = "/Test1/andrana", method = "GET")
    public ModelAndView andrana() {
        List<String> fruits = new ArrayList<>();
        fruits.add("Pomme");
        fruits.add("Banane");
        fruits.add("Mangue");
        fruits.add("Litchi");

        ModelAndView mv = new ModelAndView("liste"); // juste le nom, sans prefixe/suffixe
        mv.addObject("fruits", fruits);
        return mv;
    }
}