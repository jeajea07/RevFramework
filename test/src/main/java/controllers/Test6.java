package controllers;

import java.util.Map;

import rev.annotation.controller.Controller;
import rev.annotation.controller.UrlMapping;
import rev.annotation.controller.WebApi;
import rev.utils.RevSession;

@Controller
public class Test6 {

    @WebApi
    @UrlMapping(value = "/api/counter", method = "GET")
    public Map<String, Object> counter(RevSession session) {
        Integer count = session.getAttribute("requestCount", Integer.class);
        count = (count == null) ? 1 : count + 1;
        session.setAttribute("requestCount", count);
        return Map.of("requestCount", count, "sessionId", session.getId());
    }

    @WebApi
    @UrlMapping(value = "/api/session", method = "GET")
    public Map<String, Object> session(RevSession session) {
        return session.getAttributes();
    }

    @WebApi
    @UrlMapping(value = "/api/session/reset", method = "GET")
    public String reset(RevSession session) {
        session.clear();
        return "OK";
    }
}