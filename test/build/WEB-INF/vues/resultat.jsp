<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Résultat</title></head>
<body>
    <h2>Fruit ajouté</h2>
    <p><b>Nom :</b> ${nom}</p>
    <p><b>Quantité :</b> ${quantite}</p>
    <a href="${pageContext.request.contextPath}/Test4/formulaire">Ajouter un autre</a>
</body>
</html>