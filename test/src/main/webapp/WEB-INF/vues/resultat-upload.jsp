<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Résultat de l'upload</title>
</head>
<body>
    <h2>Fichier reçu</h2>
    <p><b>Description :</b> ${description}</p>
    <p><b>Nom :</b> ${fichier.nom}</p>
    <p><b>Extension :</b> ${fichier.extension}</p>
    <p><b>Taille :</b> ${fichier.taille} octets</p>
    <p><a href="${pageContext.request.contextPath}/Test5/formulaire">Envoyer un autre fichier</a></p>
</body>
</html>
