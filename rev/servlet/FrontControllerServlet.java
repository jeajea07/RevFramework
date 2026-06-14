package rev.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FrontControllerServlet extends HttpServlet {

    void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String method = req.getMethod();
        String url    = req.getRequestURI();
        String query  = req.getQueryString();

        out.println("<p>Tongasoa ato amin'ny <b>" + method + "</b></p>");
        out.println("<p><b>URI   :</b> " + url   + "</p>");
        out.println("<p><b>Query :</b> " + query + "</p>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");  
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        processRequest(req, res);
    }
}