<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head><title>Liste des fruits</title></head>
<body>
    <h1>Liste</h1>
    <ul>
        <%
            List<String> fruits = (List<String>) request.getAttribute("fruits");
            for (String f : fruits) {
        %>
            <li><%= f %></li>
        <%
            }
        %>
    </ul>
</body>
</html>