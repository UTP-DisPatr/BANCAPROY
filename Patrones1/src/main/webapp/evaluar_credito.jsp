<%-- 
    Document   : evaluar_credito
    Created on : 3 dic. 2025, 7:20:05 p. m.
    Author     : JHEINS
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Evaluar Solicitud</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; padding-top: 50px; }
        .card { border: none; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .preview-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd; margin-right: 15px; }
    </style>
</head>
<body>

<%
    // --- CONEXIÓN ---
    String url = "jdbc:mysql://localhost:3306/sistema_creditos"; // <--- VERIFICA EL NOMBRE DE TU BD
    String usuario = "root"; 
    String clave = ""; 
    
    Connection con = null;
    PreparedStatement psSol = null;
    PreparedStatement psDocs = null;
    ResultSet rsSol = null;
    ResultSet rsDocs = null;

    String idSolicitud = request.getParameter("id");
    
    // Variables
    String nombreCliente = "Desconocido";
    int idCliente = 0; // <--- NUEVO: Necesario para el Servlet
    double monto = 0.0;
    String tipo = "";
    String fecha = "";
    
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url, usuario, clave);

        // 1. CORRECCIÓN SQL: Agregamos 's.id_cliente' a la consulta
        String sql = "SELECT s.id_cliente, s.monto, s.tipo_credito, s.fecha_solicitud, c.nombre " +
                     "FROM solicitudes_credito s " +
                     "INNER JOIN cliente c ON s.id_cliente = c.id_cliente " +
                     "WHERE s.id_solicitud = ?";
                     
        psSol = con.prepareStatement(sql);
        psSol.setString(1, idSolicitud);
        rsSol = psSol.executeQuery();

        if (rsSol.next()) {
            idCliente = rsSol.getInt("id_cliente"); // <--- RECUPERAMOS EL ID
            nombreCliente = rsSol.getString("nombre");
            monto = rsSol.getDouble("monto");
            tipo = rsSol.getString("tipo_credito");
            fecha = rsSol.getString("fecha_solicitud");
        }
%>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card p-4">
                
                <h3 class="mb-4">Evaluando Solicitud #<%= idSolicitud %></h3>

                <div class="row mb-2">
                    <div class="col-4 fw-bold">Cliente:</div>
                    <div class="col-8"><%= nombreCliente %> (ID: <%= idCliente %>)</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Monto Solicitado:</div>
                    <div class="col-8">S/ <%= monto %></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Tipo:</div>
                    <div class="col-8"><%= tipo %></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Fecha:</div>
                    <div class="col-8"><%= fecha %></div>
                </div>

                <hr>

                <%-- SECCIÓN DE DOCUMENTOS (Sin cambios, solo visual) --%>
                <div class="bg-light p-3 rounded mb-4">
                    <h5 class="mb-3">📂 Documentos Adjuntos</h5>
                    <%
                        String sqlDocs = "SELECT * FROM documentos_solicitud WHERE id_solicitud = ?";
                        psDocs = con.prepareStatement(sqlDocs);
                        psDocs.setString(1, idSolicitud);
                        rsDocs = psDocs.executeQuery();
                    %>
                    <div class="list-group">
                        <% while (rsDocs.next()) { 
                            String nombreArchivo = rsDocs.getString("nombre_archivo");
                            String tipoDoc = rsDocs.getString("tipo_documento");
                        %>
                            <div class="list-group-item d-flex align-items-center">
                                <img src="verImagen?archivo=<%= nombreArchivo %>" class="preview-img" alt="IMG">
                                <div>
                                    <div class="fw-bold"><%= tipoDoc %></div>
                                    <small class="text-muted"><%= nombreArchivo %></small>
                                </div>
                            </div>
                        <% } %>
                    </div>
                </div>

                <%-- 2. CORRECCIÓN FORMULARIOS: Enviar datos al Servlet Correcto --%>
                <div class="d-flex justify-content-between gap-3">
                    
                    <form action="GestionAnalistaServlet" method="post" style="flex: 1;">
                        <input type="hidden" name="accion" value="rechazar">
                        <input type="hidden" name="idSolicitud" value="<%= idSolicitud %>"> 
                        <input type="hidden" name="idCliente" value="0">
                        <input type="hidden" name="monto" value="0">
                        <input type="hidden" name="cuotas" value="0">
                        
                        <button type="submit" class="btn btn-danger w-100">✕ RECHAZAR</button>
                    </form>

                    <form action="GestionAnalistaServlet" method="post" style="flex: 1;">
                        <input type="hidden" name="accion" value="aprobar">
                        
                        <input type="hidden" name="idSolicitud" value="<%= idSolicitud %>">
                        <input type="hidden" name="idCliente" value="<%= idCliente %>">
                        <input type="hidden" name="monto" value="<%= monto %>">

                        <div class="input-group mb-2">
                            <span class="input-group-text">Cuotas:</span>
                            <input type="number" name="cuotas" value="12" min="1" max="36" class="form-control" required>
                        </div>

                        <button type="submit" class="btn btn-success w-100">✓ APROBAR</button>
                    </form>

                </div>

            </div>
        </div>
    </div>
</div>

<%
    } catch (Exception e) {
%>
    <div class="alert alert-danger m-5">Error: <%= e.getMessage() %></div>
<%
    } finally {
        if (rsSol != null) rsSol.close();
        if (rsDocs != null) rsDocs.close();
        if (psSol != null) psSol.close();
        if (psDocs != null) psDocs.close();
        if (con != null) con.close();
    }
%>
</body>
</html>