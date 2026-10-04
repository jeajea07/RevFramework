<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/Test4/ajouter" method="post">
        <input type="text" name="nom" />
        <input type="number" name="quantite" />
        <button type="submit">Envoyer</button>
    </form>
</body>
</html>