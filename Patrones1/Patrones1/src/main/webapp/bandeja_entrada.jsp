<%-- 
    Document   : bandeja_entrada
    Created on : 27 nov. 2025, 5:21:02 p. m.
    Author     : JHEINS
--%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Solicitud"%>
<%@page import="Dao.SolicitudDAO"%>
<%@page import="Modelo.Analista"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bandeja del Analista</title>
        <style>
            body { font-family: sans-serif; padding: 20px; }
            .header { background-color: #007bff; color: white; padding: 15px; border-radius: 5px; }
            .alerta { color: red; font-weight: bold; }
            table { width: 100%; border-collapse: collapse; margin-top: 20px; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
            th { background-color: #f2f2f2; }
        </style>
    </head>
    <body>

        <%-- 1. SEGURIDAD: VERIFICAR SI HAY SESIÓN --%>
        <%
            // Recuperamos el objeto guardado en el Servlet
            // Asegúrate de usar el paquete correcto (Modelo.Analista o com.banco.modelo.Analista)
            Analista analista = (Analista) session.getAttribute("analistaLogueado");

            if (analista == null) {
                // Si es nulo, es un intruso. Lo pateamos al login.
                response.sendRedirect("login_analista.jsp?error=sesion");
                return;
            }
        %>

        <%-- 2. CONTENIDO PARA EL ANALISTA --%>
        <div class="header">
            <h1>Bienvenido, <%= analista.getNombre() %></h1>
            <p>Rol: <%= analista.getRol() %> | <a href="login_analista.jsp" style="color: white;">Cerrar Sesión</a></p>
        </div>

        <h3>Solicitudes de Crédito Pendientes</h3>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Tipo</th>
                <th>Cliente</th>
                <th>Monto</th>
                <th>Estado</th>
                <th>Acción</th>
            </tr>
        </thead>
        <tbody>
            <% 
                SolicitudDAO daoSolicitud = new SolicitudDAO();
                List<Solicitud> lista = daoSolicitud.listarPendientes();
                
                if(lista.isEmpty()) {
            %>
                <tr>
                    <td colspan="7" style="padding: 30px; color: #777;">
                        No hay solicitudes pendientes por ahora.
                    </td>
                </tr>
            <%  } else { 
                    for(Solicitud s : lista) { 
            %>
                <tr>
                    <td>#<%= s.getIdSolicitud() %></td>
                    
                    <td><%= s.getFechaSolicitud() %></td>
                    
                    <td style="font-weight: bold; color: #007bff;"><%= s.getTipoCredito() %></td>
                    
                    <td style="text-align: left; padding-left: 20px;"> <%= s.getNombreCliente() %></td>
                    
                    <td style="font-weight: bold;">S/ <%= String.format("%.2f", s.getMonto()) %></td>
                    
                    <td><span class="estado-pendiente"><%= s.getEstado() %></span></td>
                    
                    <td>
                        <form action="evaluar_credito.jsp" method="GET">
                            <input type="hidden" name="id" value="<%= s.getIdSolicitud() %>">
                            <button type="submit" class="btn-evaluar">Evaluar ➜</button>
                        </form>
                    </td>
                </tr>
                <%      } 
                } 
            %>
        </tbody>
    </table>

    </body>
</html>