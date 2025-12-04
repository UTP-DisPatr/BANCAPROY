<%-- 
    Document   : subir_documentos
    Created on : 3 dic. 2025, 6:40:52 p. m.
    Author     : JHEINS
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Subir Documentos | Banco Seguro</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f6f9; padding: 20px; }
        .container { max-width: 600px; margin: 0 auto; background: white; padding: 40px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }
        h2 { text-align: center; color: #333; }
        .info { background: #e3f2fd; color: #0d47a1; padding: 15px; border-radius: 6px; margin-bottom: 25px; text-align: center; }
        
        label { display: block; margin-top: 20px; font-weight: bold; color: #555; }
        input[type="file"] { margin-top: 5px; width: 100%; padding: 10px; background: #f8f9fa; border: 1px dashed #ccc; border-radius: 5px; }
        
        .btn-subir { 
            width: 100%; padding: 15px; margin-top: 30px; 
            background-color: #28a745; color: white; border: none; border-radius: 8px; 
            font-size: 1.1rem; font-weight: bold; cursor: pointer; transition: 0.3s;
        }
        .btn-subir:hover { background-color: #218838; }
    </style>
</head>
<body>
    <div class="container">
        <h2>📂 Documentación Requerida</h2>
        
        <div class="info">
            <p><strong>Solicitud #<%= request.getAttribute("idSolicitud") %> Iniciada</strong></p>
            <p>Para que un analista revise tu caso, necesitamos los siguientes sustentos.</p>
        </div>

        <form action="SubirDocumentoServlet" method="POST" enctype="multipart/form-data">
            
            <input type="hidden" name="idSolicitud" value="<%= request.getAttribute("idSolicitud") %>">

            <label>1. Documento de Identidad (DNI - Ambas caras)</label>
            <input type="file" name="fileDni" accept=".jpg,.png,.pdf" required>

            <label>2. Recibo de Servicios (Luz o Agua)</label>
            <input type="file" name="fileRecibo" accept=".jpg,.png,.pdf" required>

            <label>3. Sustento de Ingresos (Boletas de pago)</label>
            <input type="file" name="fileBoleta" accept=".jpg,.png,.pdf" required>

            <button type="submit" class="btn-subir">Subir Archivos y Finalizar</button>
        </form>
    </div>
</body>
</html>