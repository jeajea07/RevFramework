package controllers;

import java.util.Arrays;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;
import rev.utils.RevSession;

@Controller
public class Test9 {

    @WebApi
    @UrlMapping(value = "/auth/login", method = "GET")
    public String login(String roles, String type, RevSession session) {
        if (type.equals("list")) {
            session.setAttribute("userRoles", Arrays.asList(roles.split(",")));
        } else {
            session.setAttribute("userRoles", roles);
        }
        return "OK";
    }

    @WebApi
    @UrlMapping(value = "/auth/logout", method = "GET")
    public String logout(RevSession session) {
        session.removeAttribute("userRoles");
        return "OK";
    }
}