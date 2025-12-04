<%-- 
    Document   : transaccion_simple
    Created on : 4 dic. 2025, 12:11:21 a. m.
    Author     : JHEINS
--%>

<%@page import="Modelo.Cliente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Transacción Simple</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; padding: 20px; }
        .container { max-width: 400px; margin: 50px auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }
        h2 { text-align: center; color: #333; margin-bottom: 20px; }
        input[type="number"] { width: 100%; padding: 12px; margin-top: 8px; border: 1px solid #ced4da; border-radius: 6px; box-sizing: border-box; font-size: 1rem; }
        .btn-submit { width: 100%; padding: 14px; margin-top: 20px; border: none; border-radius: 8px; color: white; font-weight: bold; cursor: pointer; font-size: 1rem; }
        .btn-deposit { background-color: #007bff; }
        .btn-withdraw { background-color: #dc3545; }
        .error-msg { color: #dc3545; background: #f8d7da; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 20px; }
    </style>
</head>
<body>
    <%-- Validar Sesión --%>
    <%
        Cliente c = (Cliente) session.getAttribute("cliente");
        if(c == null) { response.sendRedirect("login_cliente.jsp"); return; }
        
        String tipo = request.getParameter("tipo"); // Viene de home_cliente.jsp (DEPOSITO o RETIRO)
        String titulo = "DEPOSITAR";
        String claseBtn = "btn-deposit";
        
        if ("RETIRO".equals(tipo)) {
            titulo = "RETIRAR";
            claseBtn = "btn-withdraw";
        }
        
        String errorMsg = request.getParameter("error");
    %>
    
    <div class="container">
        <h2><%= titulo %> Fondos</h2>
        
        <% if ("saldo_insuficiente".equals(errorMsg)) { %>
            <div class="error-msg">⚠️ Saldo insuficiente para realizar el retiro.</div>
        <% } %>
        <% if ("monto_invalido".equals(errorMsg)) { %>
            <div class="error-msg">⚠️ Ingrese un monto positivo y válido.</div>
        <% } %>
        
        <p style="text-align: center; color: #555;">Saldo Actual: S/ <%= String.format("%.2f", c.getSaldo()) %></p>
        
        <form action="TransaccionServlet" method="POST">
            <input type="hidden" name="accion" value="<%= tipo %>">
            
            <label for="monto">Monto a <%= titulo.toLowerCase() %> (S/)</label>
            <input type="number" id="monto" name="monto" min="0.01" step="0.01" required placeholder="Ej. 100.00">
            
            <button type="submit" class="btn-submit <%= claseBtn %>">Confirmar <%= titulo %></button>
        </form>
        
        <a href="HomeClienteServlet" style="display: block; text-align: center; margin-top: 15px;">← Volver</a>
    </div>
</body>
</html>