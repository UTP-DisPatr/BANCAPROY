<%@page import="Modelo.Cliente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Solicitar Crédito | Banco Seguro</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; padding: 20px; }
        
        .container { 
            max-width: 600px; margin: 20px auto; background: white; 
            padding: 40px; border-radius: 12px; 
            box-shadow: 0 10px 25px rgba(0,0,0,0.1); 
        }
        
        h2 { text-align: center; color: #333; margin-bottom: 5px; }
        .subtitle { text-align: center; color: #777; margin-bottom: 30px; font-size: 0.95rem; }
        
        /* Estilos de Inputs */
        label { display: block; margin-top: 15px; font-weight: 600; color: #495057; font-size: 0.9rem; }
        input[type="number"], select { 
            width: 100%; padding: 12px; margin-top: 8px; 
            border: 1px solid #ced4da; border-radius: 6px; 
            box-sizing: border-box; font-size: 1rem; transition: border-color 0.2s;
        }
        input:focus, select:focus { border-color: #007bff; outline: none; }
        
        /* Grupo de Botones */
        .btn-group { display: flex; gap: 15px; margin-top: 30px; }
        button { 
            flex: 1; padding: 14px; border: none; border-radius: 8px; 
            color: white; font-weight: bold; cursor: pointer; 
            font-size: 1rem; transition: transform 0.1s, opacity 0.2s; 
        }
        
        .btn-simular { background-color: #17a2b8; box-shadow: 0 4px 6px rgba(23, 162, 184, 0.2); }
        .btn-solicitar { background-color: #28a745; box-shadow: 0 4px 6px rgba(40, 167, 69, 0.2); }
        
        button:hover { opacity: 0.9; transform: translateY(-2px); }
        button:active { transform: translateY(0); }

        /* Caja de Resultados */
        .resultado-box {
            background-color: #f8f9fa; border-left: 5px solid #28a745;
            padding: 20px; margin-top: 30px; border-radius: 6px;
            animation: fadeIn 0.5s ease-in-out;
        }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        
        .resultado-box h3 { margin-top: 0; color: #28a745; font-size: 1.1rem; }
        .res-dato { display: flex; justify-content: space-between; margin-bottom: 8px; color: #555; }
        .res-total { text-align: center; margin-top: 15px; padding-top: 15px; border-top: 1px solid #ddd; }
        .res-total h1 { color: #333; margin: 5px 0; font-size: 2.5rem; }

        /* Botón Volver */
        .btn-volver { 
            display: block; text-align: center; margin-top: 25px; 
            color: #6c757d; text-decoration: none; font-size: 0.9rem; 
        }
        .btn-volver:hover { color: #343a40; text-decoration: underline; }
        
        /* Mensaje de Error */
        .error-msg { color: #dc3545; background: #f8d7da; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 20px; }
    </style>
</head>
<body>

    <%-- 1. SEGURIDAD: Validar Sesión --%>
    <%
        Cliente c = (Cliente) session.getAttribute("cliente");
        if(c == null) { response.sendRedirect("login_cliente.jsp"); return; }
    %>

    <div class="container">
        <h2>Solicitud de Crédito</h2>
        <p class="subtitle">Hola <b><%= c.getNombre() %></b>, simula y elige tu mejor opción.</p>

        <%-- Mensaje de Error si los datos son inválidos --%>
        <% if (request.getParameter("error") != null) { %>
            <div class="error-msg">⚠️ Por favor, ingresa montos y cuotas válidos.</div>
        <% } %>

        <form action="SolicitudCreditoServlet" method="POST">
            
            <label>¿Cuánto dinero necesitas? (S/)</label>
            <input type="number" name="monto" min="100" step="0.01" required placeholder="Ej. 5000.00"
                   value="<%= request.getAttribute("res_monto") != null ? request.getAttribute("res_monto") : "" %>">

            <label>Plazo de pago (Meses)</label>
            <input type="number" name="cuotas" min="1" max="60" required placeholder="Ej. 12"
                   value="<%= request.getAttribute("res_cuotas") != null ? request.getAttribute("res_cuotas") : "" %>">

            <label>Tipo de Crédito</label>
            <select name="tipoCredito">
                <% 
                    String tipoSel = (String) request.getAttribute("res_tipo"); 
                    if (tipoSel == null) tipoSel = "";
                %>
                <option value="PERSONAL" <%= tipoSel.equals("PERSONAL") ? "selected" : "" %>>Préstamo Personal (Libre Disponibilidad)</option>
                <option value="VEHICULAR" <%= tipoSel.equals("VEHICULAR") ? "selected" : "" %>>Crédito Vehicular (Auto Nuevo)</option>
                <option value="HIPOTECARIO" <%= tipoSel.equals("HIPOTECARIO") ? "selected" : "" %>>Crédito Hipotecario (Vivienda)</option>
                <option value="ESTUDIOS" <%= tipoSel.equals("ESTUDIOS") ? "selected" : "" %>>Crédito Educativo</option>
            </select>

            <div class="btn-group">
                <button type="submit" name="accion" value="simular" class="btn-simular">📊 Simular Cuota</button>
                
                <button type="submit" name="accion" value="solicitar" class="btn-solicitar">✅ Solicitar Crédito</button>
            </div>
        </form>

        <%-- ZONA DE RESULTADOS (Solo aparece si el Servlet envió respuesta) --%>
        <% 
            if (request.getAttribute("res_valor_cuota") != null) {
                double tasa = (double) request.getAttribute("res_tasa");
                double cuota = (double) request.getAttribute("res_valor_cuota");
        %>
            <div class="resultado-box">
                <h3>Resumen de Simulación</h3>
                
                <div class="res-dato">
                    <span>Monto Solicitado:</span>
                    <b>S/ <%= request.getAttribute("res_monto") %></b>
                </div>
                <div class="res-dato">
                    <span>Plazo:</span>
                    <b><%= request.getAttribute("res_cuotas") %> meses</b>
                </div>
                <div class="res-dato">
                    <span>Tasa Anual (TEA):</span>
                    <b style="color: #007bff;"><%= tasa %>%</b>
                </div>

                <div class="res-total">
                    <small>Tu cuota mensual aproximada será:</small>
                    <h1>S/ <%= String.format("%.2f", cuota) %></h1>
                </div>
            </div>
        <% } %>

        <a href="HomeClienteServlet" class="btn-volver">← Volver al Inicio</a>
    </div>

</body>
</html>