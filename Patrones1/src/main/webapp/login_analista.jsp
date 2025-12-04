<%-- 
    Document   : login_Analista
    Created on : 26 nov. 2025, 20:40:04
    Author     : Alessandro
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login Analista</title>
    <link rel="stylesheet" href="Css/style.css">
</head>
<body>

<header class="header">
    <h2 class="logo">Sistema Bancario</h2>
</header>

<div class="container">

    <div class="card">

        <h2 class="titulo">Acceso Analista</h2>
        <p class="subtitulo">Ingresa con tus credenciales</p>

        <form action="LoginAnalistaServlet" method="post" class="form">

            <label>Usuario</label>
            <input type="text" name="usuario" required>

            <label>Contraseña</label>
            <input type="password" name="password" required>

            <button type="submit" class="btn">Ingresar</button>

        </form>

        <% if(request.getParameter("error") != null) { %>
            <div class="alert error">
                Usuario o contraseña incorrectos
            </div>
        <% } %>

    </div>

</div>

</body>
</html>

