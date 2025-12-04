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
        .doc-item { padding: 10px; border-bottom: 1px solid #eee; }
        .preview-img { 
            width: 80px; height: 80px; 
            object-fit: cover; /* Para que la foto no se estire feo */
            border-radius: 4px; 
            border: 1px solid #ddd;
            margin-right: 15px;
        }
    </style>
</head>
<body>

<%
    // --- CONEXIÓN ---
    String url = "jdbc:mysql://localhost:3306/sistema_creditos"; 
    String usuario = "root"; 
    String clave = ""; 
    
    Connection con = null;
    PreparedStatement psSol = null;
    PreparedStatement psDocs = null;
    ResultSet rsSol = null;
    ResultSet rsDocs = null;

    String idSolicitud = request.getParameter("id");
    
    // Variables por defecto
    String nombreCliente = "Desconocido";
    double monto = 0.0;
    String tipo = "";
    String fecha = "";
    
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url, usuario, clave);

        // --- CORRECCIÓN AQUÍ: USAMOS JOIN PARA OBTENER EL NOMBRE DEL CLIENTE ---
        // Asumo que tu tabla se llama 'cliente' y el campo del nombre es 'nombres'
        // Si tu columna se llama diferente (ej: 'nombre', 'razon_social'), cámbialo abajo.
        String sql = "SELECT s.monto, s.tipo_credito, s.fecha_solicitud, c.nombre " +  // <--- AQUÍ
                     "FROM solicitudes_credito s " +
                     "INNER JOIN cliente c ON s.id_cliente = c.id_cliente " +
                     "WHERE s.id_solicitud = ?";

        psSol = con.prepareStatement(sql);
        psSol.setString(1, idSolicitud);
        rsSol = psSol.executeQuery();

        if (rsSol.next()) {
            // CAMBIO 2: Al recuperar el dato (debe coincidir exactamente con el de arriba)
            nombreCliente = rsSol.getString("nombre"); // <--- Y AQUÍ
            monto = rsSol.getDouble("monto");
            tipo = rsSol.getString("tipo_credito"); // En tu captura la columna es 'tipo_credito'
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
                    <div class="col-8"><%= nombreCliente %></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Monto:</div>
                    <div class="col-8">S/ <%= monto %></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Tipo Crédito:</div>
                    <div class="col-8"><%= tipo %></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Fecha:</div>
                    <div class="col-8"><%= fecha %></div>
                </div>

                <hr>

                <div class="bg-light p-3 rounded">
                    <h5 class="mb-3">📂 Documentos Adjuntos</h5>
                    
                    <%
                        // Consulta para traer los archivos de esta solicitud
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
                                <img src="verImagen?archivo=<%= nombreArchivo %>" class="preview-img" alt="Foto">
                                
                                <div>
                                    <div class="fw-bold"><%= tipoDoc %></div>
                                    <small class="text-muted"><%= nombreArchivo %></small> <br>
                                    <a href="verImagen?archivo=<%= nombreArchivo %>" target="_blank" class="text-primary text-decoration-none small">
                                        Ver tamaño completo
                                    </a>
                                </div>
                            </div>
                        <% } %>
                    </div>
                </div>
                    <div class="d-flex justify-content-between mt-4">
    
                    <form action="ProcesarSolicitudServlet" method="post" style="width: 48%;">
                        <input type="hidden" name="id" value="<%= idSolicitud %>">
                        <input type="hidden" name="accion" value="rechazar">
                        <button type="submit" class="btn btn-danger w-100">✕ RECHAZAR</button>
                    </form>

                    <form action="ProcesarSolicitudServlet" method="post" style="width: 48%;">
                        <input type="hidden" name="id" value="<%= idSolicitud %>">
                        <input type="hidden" name="accion" value="aprobar">
                        <button type="submit" class="btn btn-success w-100">✓ APROBAR Y DESEMBOLSAR</button>
                    </form>

                </div>

            </div>
        </div>
    </div>
</div>

<%
    } catch (Exception e) {
%>
    <div class="alert alert-danger m-5">
        <h4>Error en el sistema</h4>
        <p><%= e.getMessage() %></p>
        <pre><%= e.toString() %></pre>
    </div>
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