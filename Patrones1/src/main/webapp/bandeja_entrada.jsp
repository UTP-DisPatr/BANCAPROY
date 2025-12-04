<%-- 
    Document   : bandeja_entrada
    Created on : 27 nov. 2025, 5:21:02 p. m.
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
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body { font-family: sans-serif; padding: 20px; background-color: #f8f9fa; }
            .header { background-color: #007bff; color: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
            table { width: 100%; border-collapse: collapse; background: white; }
            th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
            th { background-color: #e9ecef; }
            .btn-evaluar {
                background-color: #28a745; color: white; border: none; padding: 5px 10px; 
                border-radius: 3px; cursor: pointer; text-decoration: none;
            }
        </style>
    </head>
    <body>

        <%-- 1. SEGURIDAD: VERIFICAR SI HAY SESIÓN --%>
        <%
            Analista analista = (Analista) session.getAttribute("analistaLogueado");
            if (analista == null) {
                response.sendRedirect("login_analista.jsp?error=sesion");
                return;
            }
        %>

        <%-- 2. ENCABEZADO --%>
        <div class="header">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1 class="h3 m-0">Bienvenido, <%= analista.getNombre() %></h1>
                    <small>Rol: <%= analista.getRol() %></small>
                </div>
                <a href="login_analista.jsp" class="btn btn-light btn-sm">Cerrar Sesión</a>
            </div>
        </div>

        <%-- 3. ZONA DE MENSAJES INTELIGENTE (Lee Sesión y URL) --%>
        <% 
            // A. Intentamos leer de la SESIÓN (Prioridad: Observer)
            String msg = (String) session.getAttribute("mensajeExito");
            String err = (String) session.getAttribute("mensajeError");
            
            // B. BACKUP: Si no hay nada en sesión, leemos de la URL
            // (Esto arregla el error si tu Servlet antiguo envía ?msg=aprobado)
            String paramMsg = request.getParameter("msg");
            if (msg == null && paramMsg != null) {
                if (paramMsg.equals("aprobado")) {
                    msg = "¡Crédito Aprobado exitosamente!";
                } else if (paramMsg.equals("rechazado")) {
                    msg = "Crédito Rechazado correctamente.";
                }
            }
            
            // C. MOSTRAR EL MENSAJE
            if (msg != null && !msg.isEmpty()) { 
        %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <strong>✓ Notificación del Sistema:</strong> <%= msg %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% 
                session.removeAttribute("mensajeExito"); // Limpiar sesión para que no salga doble
            } 
            
            if (err != null && !err.isEmpty()) {
        %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong>✕ Error:</strong> <%= err %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% 
                session.removeAttribute("mensajeError"); 
            }
        %>

        <h3>Solicitudes de Crédito Pendientes</h3>

        <%-- 4. TABLA DE SOLICITUDES --%>
        <div class="card shadow-sm">
            <div class="card-body p-0">
                <table class="table table-hover m-0">
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
                                <td colspan="7" class="text-center p-4 text-muted">
                                    No hay solicitudes pendientes por ahora.
                                </td>
                            </tr>
                        <%  } else { 
                                for(Solicitud s : lista) { 
                        %>
                            <tr>
                                <td>#<%= s.getIdSolicitud() %></td>
                                <td><%= s.getFechaSolicitud() %></td>
                                <td class="text-primary fw-bold"><%= s.getTipoCredito() %></td>
                                <td><%= s.getNombreCliente() %></td>
                                <td class="fw-bold">S/ <%= String.format("%.2f", s.getMonto()) %></td>
                                <td><span class="badge bg-warning text-dark"><%= s.getEstado() %></span></td>
                                <td>
                                    <form action="evaluar_credito.jsp" method="GET">
                                        <input type="hidden" name="id" value="<%= s.getIdSolicitud() %>">
                                        <button type="submit" class="btn btn-primary btn-sm">Evaluar ➜</button>
                                    </form>
                                </td>
                            </tr>
                        <%      } 
                            } 
                        %>
                    </tbody>
                </table>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>