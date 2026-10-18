<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Upload de fichier</title>
</head>
<body>
    <h2>Envoyer un fichier</h2>
    <form action="${pageContext.request.contextPath}/Test5/envoyer"
          method="post" enctype="multipart/form-data">
        <p>
            <label>Description :</label>
            <input type="text" name="description">
        </p>
        <p>
            <label>Nom du fichier :</label>
            <input type="text" name="nomFichier">
        </p>
        <p>
            <label>Fichier :</label>
            <input type="file" name="fichier">
        </p>
        <button type="submit">Envoyer</button>
    </form>
</body>
</html>
