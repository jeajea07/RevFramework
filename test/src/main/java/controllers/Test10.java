package controllers;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;

@Controller
public class Test10 {

    private int compteur = 0;

    @WebApi
    @UrlMapping(value = "/singleton/compter", method = "GET")
    public int compter() {
        compteur++;
        return compteur;
    }
}