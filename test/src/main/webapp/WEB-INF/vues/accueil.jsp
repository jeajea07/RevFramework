<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Enumeration" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test6 - Gestion de session</title>
    <style>
        body { font-family: sans-serif; max-width: 700px; margin: 30px auto; }
        fieldset { margin-bottom: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #999; padding: 6px 10px; text-align: left; }
        .msg { background: #e8f5e9; padding: 8px; margin-bottom: 15px; }
        .vide { color: #888; font-style: italic; }
    </style>
</head>
<body>

<h2>Test6 : gestion de session (MySession)</h2>

<% if (request.getAttribute("message") != null) { %>
    <div class="msg"><%= request.getAttribute("message") %></div>
<% } %>

<fieldset>
    <legend>Ajouter dans la session</legend>
    <form method="POST" action="<%= request.getContextPath() %>/Test6/accueil/ajouter">
        Clé : <input type="text" name="cle" required>
        Valeur : <input type="text" name="valeur" required>
        <button type="submit">Ajouter</button>
    </form>
</fieldset>

<fieldset>
    <legend>Supprimer de la session</legend>
    <form method="POST" action="<%= request.getContextPath() %>/Test6/accueil/supprimer">
        Clé : <input type="text" name="cle" required>
        <button type="submit">Supprimer</button>
    </form>
</fieldset>

<fieldset>
    <legend>Contenu de la vraie HttpSession</legend>
    <p>ID de session : <b><%= session.getId() %></b></p>
    <%
        Enumeration<String> noms = session.getAttributeNames();
        if (!noms.hasMoreElements()) {
    %>
        <p class="vide">La session est vide.</p>
    <% } else { %>
        <table>
            <tr><th>Clé</th><th>Valeur</th></tr>
            <% while (noms.hasMoreElements()) {
                   String nom = noms.nextElement(); %>
                <tr>
                    <td><%= nom %></td>
                    <td><%= session.getAttribute(nom) %></td>
                </tr>
            <% } %>
        </table>
    <% } %>
</fieldset>

<p><a href="<%= request.getContextPath() %>/Test6/accueil/afficher">Rafraîchir (GET /Test6/accueil/afficher)</a></p>

</body>
</html>