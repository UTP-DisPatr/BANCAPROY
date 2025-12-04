import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/verImagen") 
public class ImagenServlet extends HttpServlet {

    private static final String RUTA_BASE = "C:/banco_uploads/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Obtenemos el nombre del archivo que nos piden
        String nombreArchivo = request.getParameter("archivo");
        
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. Buscamos el archivo en la carpeta del servidor
        File archivo = new File(RUTA_BASE, nombreArchivo);

        if (!archivo.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 3. Configuramos el tipo de respuesta (MIME type)
        String tipoMime = getServletContext().getMimeType(archivo.getName());
        if (tipoMime == null) {
            tipoMime = "application/octet-stream";
        }
        response.setContentType(tipoMime);

        // 4. Copiamos los bytes del archivo a la respuesta para que el navegador lo vea
        Files.copy(archivo.toPath(), response.getOutputStream());
    }
}