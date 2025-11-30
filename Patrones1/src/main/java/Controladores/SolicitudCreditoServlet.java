package Controladores;

import Dao.SolicitudDAO;
import Modelo.Cliente;
import Modelo.Solicitud;
import Modelo.SimuladorCredito; // Asegúrate de haber creado esta clase en el Paso 3
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "SolicitudCreditoServlet", urlPatterns = {"/SolicitudCreditoServlet"})
public class SolicitudCreditoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtenemos la acción del botón (simular o solicitar)
        String accion = request.getParameter("accion");
        
        // ---------------------------------------------------------
        // CASO 1: SIMULACIÓN DE CRÉDITO (Matemática pura)
        // ---------------------------------------------------------
        if ("simular".equals(accion)) {
            try {
                double monto = Double.parseDouble(request.getParameter("monto"));
                int cuotas = Integer.parseInt(request.getParameter("cuotas"));
                double tasaAnual = 15.0; // Puedes traer esto de la BD si prefieres

                // Llamamos a la lógica de negocio (Backend)
                SimuladorCredito simulador = new SimuladorCredito();
                double cuotaMensual = simulador.calcularCuota(monto, cuotas, tasaAnual);

                // Enviamos los resultados de vuelta al JSP para que el usuario los vea
                request.setAttribute("res_monto", monto);
                request.setAttribute("res_cuotas", cuotas);
                request.setAttribute("res_valor_cuota", cuotaMensual);
                
                // Redirigimos al mismo JSP para mostrar el resultado
                request.getRequestDispatcher("home_cliente.jsp").forward(request, response);

            } catch (NumberFormatException e) {
                response.sendRedirect("home_cliente.jsp?error=datos_invalidos");
            }
        } 
        
        // ---------------------------------------------------------
        // CASO 2: REGISTRAR SOLICITUD (Guardar en BD)
        // ---------------------------------------------------------
        else if ("solicitar".equals(accion)) {
            HttpSession session = request.getSession();
            // Asumo que tienes un "Cliente" o "clienteLogueado" en la sesión
            // Si tu login de cliente usa otro nombre, cámbialo aquí.
            Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");

            if (cliente != null) {
                Solicitud s = new Solicitud();
                s.setIdCliente(cliente.getId_Cliente()); // Asegúrate que Cliente tenga getIdCliente()
                s.setMonto(Double.parseDouble(request.getParameter("monto")));
                s.setTipoCredito(request.getParameter("tipoCredito"));
                s.setFechaSolicitud(new java.sql.Date(System.currentTimeMillis()));
                
                // NOTA: Al hacer 'new Solicitud()', el Patrón State
                // automáticamente le asigna el estado "PENDIENTE" (Ver Modelo/Solicitud.java)

                SolicitudDAO dao = new SolicitudDAO();
                if (dao.registrarSolicitud(s)) {
                    response.sendRedirect("exito.jsp");
                } else {
                    response.sendRedirect("error.jsp");
                }
            } else {
                // Si no hay sesión, mandar al login
                response.sendRedirect("login_cliente.jsp");
            }
        }
    }
}