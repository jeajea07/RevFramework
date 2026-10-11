<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/Test4/save" method="post">
        <input type="text" name="nom">
        <input type="text" name="prenom">
        <input type="number" name="age">
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>