package Controladores;

import Dao.SolicitudDAO;
import Modelo.Analista;
import Modelo.Solicitud;
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
        
        Analista analistaLogueado = (Analista) session.getAttribute("analistaLogueado");

        if (analistaLogueado == null) {
            response.sendRedirect("login_analista.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));

        SolicitudDAO dao = new SolicitudDAO();

        Solicitud sol = dao.obtenerPorId(idSolicitud);

        if (sol != null) {
            sol.setIdAnalista(analistaLogueado.getIdAnalista());

            try {
                Patrones.SistemaNotificacion.buzonMensajes = "";

                if ("aprobar".equals(accion)) {
                    sol.aprobar(); 
                } else if ("rechazar".equals(accion)) {
                    sol.rechazar();
                }

                String mensajeDelObserver = Patrones.SistemaNotificacion.buzonMensajes;

                if (mensajeDelObserver != null && !mensajeDelObserver.isEmpty()) {
                    session.setAttribute("mensajeExito", mensajeDelObserver);
                } else {
                    session.setAttribute("mensajeExito", "Operación realizada (Sin notificación)");
                }

                dao.actualizarEstado(sol);

            } catch (IllegalStateException e) {
                session.setAttribute("mensajeError", e.getMessage());
            } catch (Exception e) {
                session.setAttribute("mensajeError", "Error interno: " + e.getMessage());
            }
        } else {
            session.setAttribute("mensajeError", "Error: No se encontró la solicitud.");
        }

        response.sendRedirect("bandeja_entrada.jsp");
    }
}