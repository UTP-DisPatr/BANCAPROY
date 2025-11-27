<%-- 
    Document   : registroCliente
    Created on : 26 nov. 2025, 20:38:16
    Author     : Alessandro
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Registro Cliente</title>
    <link rel="stylesheet" href="Css/style.css">
</head>
<body>

<header class="header">
    <h2 class="logo">Banco Seguro</h2>
</header>

<div class="container">
    <div class="card">
        <h2>Registro</h2>

        <form action="ClienteServlet" method="post" class="form">

            <input type="text" name="dni" placeholder="DNI" maxlength="8" required>
            <input type="text" name="nombre" placeholder="Nombre" required>
            <input type="text" name="apellido" placeholder="Apellido" required>
            <input type="email" name="email" placeholder="Correo" required>
            <input type="tel" name="telefono" placeholder="Teléfono" maxlength="9" required>
            <input type="password" name="password" placeholder="Contraseña" required>

            <button type="submit" class="btn">Registrar</button>
        </form>
    </div>
</div>

</body>
</html>
