package Controladores;

import Dao.SolicitudDAO;
import Modelo.Analista;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "GestionAnalistaServlet", urlPatterns = {"/GestionAnalistaServlet"})
public class GestionAnalistaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // 1. DEFINICIÓN DE LA VARIABLE (Aquí le ponemos el nombre que usaremos abajo)
        Analista analistaLogueado = (Analista) session.getAttribute("analistaLogueado");

        // Validación de seguridad
        if (analistaLogueado == null) {
            response.sendRedirect("login_analista.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        double monto = Double.parseDouble(request.getParameter("monto"));
        int cuotas = Integer.parseInt(request.getParameter("cuotas"));

        SolicitudDAO dao = new SolicitudDAO();

        if ("aprobar".equals(accion)) {
            // 2. USO DE LA VARIABLE (Ahora sí coincide el nombre)
            boolean exito = dao.aprobarCredito(idSolicitud, idCliente, monto, cuotas, analistaLogueado.getIdAnalista());
            
            if (exito) {
                response.sendRedirect("bandeja_entrada.jsp?msg=aprobado");
            } else {
                response.sendRedirect("bandeja_entrada.jsp?error=bd");
            }

        } else if ("rechazar".equals(accion)) {
            // Para rechazar, podrías crear un método en el DAO o actualizar simple
            // dao.rechazar(idSolicitud, analistaLogueado.getIdAnalista());
            response.sendRedirect("bandeja_entrada.jsp?msg=rechazado");
        }
    }
}