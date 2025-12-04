package Controladores;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/SubirDocumentoServlet")
@MultipartConfig( // OBLIGATORIO PARA RECIBIR ARCHIVOS
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class SubirDocumentoServlet extends HttpServlet {

    // Cambia esta ruta a una carpeta real en tu PC (Ej: C:/banco_uploads)
    private static final String UPLOAD_DIR = "C:/banco_uploads"; 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Crear carpeta si no existe
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdir();

        String idSolicitud = request.getParameter("idSolicitud");

        try {
            // Guardar los 3 archivos
            // Nota: Aquí solo los guardamos en disco. Idealmente guardarías la ruta en BD.
            guardarArchivo(request.getPart("fileDni"), idSolicitud, "DNI");
            guardarArchivo(request.getPart("fileRecibo"), idSolicitud, "RECIBO");
            guardarArchivo(request.getPart("fileBoleta"), idSolicitud, "BOLETA");

            // FIN DEL PROCESO
            response.sendRedirect("HomeClienteServlet?msg=solicitud_enviada");

        } catch (Exception e) {
            System.out.println("Error subiendo: " + e.getMessage());
            response.sendRedirect("HomeClienteServlet?error=error_subida");
        }
    }
private void guardarArchivo(Part part, String idSol, String tipo) throws IOException {
        String nombreOriginal = part.getSubmittedFileName();
        
        if(nombreOriginal != null && !nombreOriginal.isEmpty()){
            // 1. Guardar Físicamente en Disco
            String nuevoNombre = "SOL_" + idSol + "_" + tipo + "_" + nombreOriginal;
            String fullPath = UPLOAD_DIR + File.separator + nuevoNombre;
            part.write(fullPath);
            
            // 2. GUARDAR EN BASE DE DATOS (Lo que faltaba)
            try {
                // Parseamos el ID a entero
                int idSolicitudInt = Integer.parseInt(idSol);
                
                Dao.SolicitudDAO dao = new Dao.SolicitudDAO();
                dao.registrarDocumento(idSolicitudInt, tipo, nuevoNombre);
                
            } catch (Exception e) {
                System.out.println("Error al registrar en BD el archivo: " + nuevoNombre);
            }
        }
    }
}
