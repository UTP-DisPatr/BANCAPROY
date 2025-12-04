package Controladores;

import Dao.SolicitudDAO;
import Modelo.Analista;
import Modelo.Solicitud; // Importante: Necesitamos el Modelo
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
        
        // 1. SEGURIDAD: Verificar Analista
        Analista analistaLogueado = (Analista) session.getAttribute("analistaLogueado");

        if (analistaLogueado == null) {
            response.sendRedirect("login_analista.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));

        SolicitudDAO dao = new SolicitudDAO();

        // 2. RECUPERAR EL OBJETO (Vital para que funcionen los patrones)
        Solicitud sol = dao.obtenerPorId(idSolicitud);

        if (sol != null) {
            // Asignamos el analista que está haciendo la acción
            sol.setIdAnalista(analistaLogueado.getIdAnalista());

            try {
                // --- LIMPIEZA DEL BUZÓN ---
                // Nos aseguramos de que el buzón esté vacío antes de empezar
                Patrones.SistemaNotificacion.buzonMensajes = "";

                // 3. EJECUCIÓN DE PATRONES (State + Observer)
                if ("aprobar".equals(accion)) {
                    sol.aprobar(); // <--- AQUÍ SE DISPARA EL OBSERVER INTERNAMENTE
                } else if ("rechazar".equals(accion)) {
                    sol.rechazar();
                }

                // 4. RECOGER EL MENSAJE DEL BUZÓN (Prueba del Observer)
                // El Servlet NO escribe el mensaje, solo lo lee del Observer.
                String mensajeDelObserver = Patrones.SistemaNotificacion.buzonMensajes;

                // Guardamos en sesión para que el JSP lo muestre en verde
                if (mensajeDelObserver != null && !mensajeDelObserver.isEmpty()) {
                    session.setAttribute("mensajeExito", mensajeDelObserver);
                } else {
                    // Si por alguna razón el Observer falló, mostramos un genérico (pero no debería pasar)
                    session.setAttribute("mensajeExito", "Operación realizada (Sin notificación)");
                }

                // 5. GUARDAR CAMBIOS EN BD
                dao.actualizarEstado(sol);

            } catch (IllegalStateException e) {
                // Si el Patrón State bloquea la acción (ej: aprobar lo ya rechazado)
                session.setAttribute("mensajeError", e.getMessage());
            } catch (Exception e) {
                session.setAttribute("mensajeError", "Error interno: " + e.getMessage());
            }
        } else {
            session.setAttribute("mensajeError", "Error: No se encontró la solicitud.");
        }

        // 6. REDIRECCIÓN LIMPIA (Sin parámetros en la URL)
        response.sendRedirect("bandeja_entrada.jsp");
    }
}