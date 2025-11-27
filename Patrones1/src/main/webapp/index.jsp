<%-- 
    Document   : index2
    Created on : 26 nov. 2025, 17:45:09
    Author     : Alessandro
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login Cliente</title>
    <link rel="stylesheet" href="Css/style.css">
</head>
<body>

<header class="header">
    <h2 class="logo">Banco Seguro</h2>
</header>

<div class="container">
    <div class="card">
        <h2>Acceso Cliente</h2>

        <form action="ClienteServlet" method="post" class="form">

            <input type="text" name="dni" placeholder="DNI" maxlength="8" required>
            <input type="password" name="password" placeholder="Contraseña" required>

            <button type="submit" class="btn">Ingresar</button>

            <a href="registroCliente.jsp" class="link">Registrarme</a>
        </form>
    </div>
</div>

</body>
</html>

