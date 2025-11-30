package Controladores;

import Dao.SolicitudDAO;
import Modelo.Analista;
import Modelo.Solicitud;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "GestionAnalistaServlet", urlPatterns = {"/GestionAnalistaServlet"})
public class GestionAnalistaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion"); // Vendrá "aprobar" o "rechazar"
        int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));

        // Recuperamos al analista de la sesión (tal como lo guardaste en LoginAnalistaServlet)
        HttpSession session = request.getSession();
        Analista analistaLogueado = (Analista) session.getAttribute("analistaLogueado");

        if (analistaLogueado == null) {
            response.sendRedirect("login_analista.jsp");
            return;
        }

        SolicitudDAO dao = new SolicitudDAO();
        
        // 1. Obtenemos la solicitud de la BD
        Solicitud sol = dao.obtenerPorId(idSolicitud);

        if (sol != null) {
            // Asignamos qué analista está trabajando
            sol.setIdAnalista(analistaLogueado.getIdAnalista()); // Asegúrate que Analista tenga este getter

            try {
                // ==========================================
                // AQUÍ ACTÚA EL PATRÓN STATE (Punto fuerte)
                // ==========================================
                // No preguntamos el estado actual. 
                // Ejecutamos la acción y el objeto decide si es válida.
                
                if ("aprobar".equals(accion)) {
                    sol.aprobar(); // Si ya estaba rechazada, esto lanzará error automáticamente
                } else if ("rechazar".equals(accion)) {
                    sol.rechazar();
                }

                // Si el patrón State no lanzó error, guardamos en BD
                dao.actualizarEstado(sol);

            } catch (IllegalStateException e) {
                // El Patrón detectó una violación de flujo (ej: aprobar lo rechazado)
                System.out.println("ERROR DE LÓGICA: " + e.getMessage());
                // Podrías pasar este error a la sesión para mostrarlo en el JSP
                session.setAttribute("mensajeError", e.getMessage());
            }
        }

        // Volvemos a la bandeja
        response.sendRedirect("bandeja_entrada.jsp");
    }
}