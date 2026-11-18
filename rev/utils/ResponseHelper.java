package rev.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseHelper {

    public static void handleUnauthorized(HttpServletResponse res, boolean api) throws IOException {
        envoyer(res, 401, "Non authentifié", "Vous devez vous connecter.", api);
    }

    public static void handleForbidden(HttpServletResponse res, boolean api) throws IOException {
        envoyer(res, 403, "Accès refusé", "Vous n'avez pas le rôle nécessaire.", api);
    }

    private static void envoyer(HttpServletResponse res, int code, String titre, String message, boolean api)
            throws IOException {
        res.setStatus(code);
        PrintWriter out = res.getWriter();

        if (api) {
            res.setContentType("application/json;charset=UTF-8");
            out.print("{\"status\":" + code + ",\"error\":\"" + titre + "\",\"message\":\"" + message + "\"}");
        } else {
            res.setContentType("text/html;charset=UTF-8");
            out.println("<html><body>");
            out.println("<h1>" + code + " - " + titre + "</h1>");
            out.println("<p>" + message + "</p>");
            out.println("</body></html>");
        }
    }
}