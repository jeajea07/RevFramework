package controllers;

import entities.Fruit;
import org.springframework.context.ApplicationContext;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.utils.ModelAndView;
import services.FruitService;

import java.util.List;

@Controller
public class Test1 {

    @UrlMapping(value = "/Test1/andrana", method = "GET")
    public ModelAndView andrana(ApplicationContext ctx) {

        FruitService fruitService = ctx.getBean(FruitService.class);
        List<Fruit> fruits = fruitService.getFruits();

        ModelAndView mv = new ModelAndView("liste");
        mv.addObject("fruits", fruits);
        return mv;
    }
}