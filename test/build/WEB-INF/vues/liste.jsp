<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="entities.Fruit" %>
<!DOCTYPE html>
<html>
<head><title>Liste des fruits</title></head>
<body>
    <h1>Liste</h1>
    <ul>
        <%
            List<Fruit> fruits = (List<Fruit>) request.getAttribute("fruits");
            for (Fruit f : fruits) {
        %>
            <li><%= f.getNom() %></li>
        <%
            }
        %>
    </ul>
</body>
</html>