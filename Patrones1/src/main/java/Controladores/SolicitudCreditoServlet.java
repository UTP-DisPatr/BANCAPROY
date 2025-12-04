package Controladores;

import Dao.SolicitudDAO;
import Modelo.Cliente;
import Modelo.Solicitud;
import Modelo.SimuladorCredito;
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
        // CASO 1: SIMULACIÓN DE CRÉDITO
        // ---------------------------------------------------------
        if ("simular".equals(accion)) {
            try {
                // 1. Recibimos datos
                double monto = Double.parseDouble(request.getParameter("monto"));
                int cuotas = Integer.parseInt(request.getParameter("cuotas"));
                
                // NUEVO: Recibimos el tipo para saber la tasa
                String tipo = request.getParameter("tipoCredito"); 

                // 2. Lógica de Negocio
                SimuladorCredito simulador = new SimuladorCredito();
                
                // A. Obtenemos la tasa dinámica
                double tasaAnual = simulador.obtenerTasaAnual(tipo); 
                
                // B. Calculamos la cuota con esa tasa
                double cuotaMensual = simulador.calcularCuota(monto, cuotas, tasaAnual);

                // 3. Enviamos TODO de vuelta al JSP
                request.setAttribute("res_monto", monto);
                request.setAttribute("res_cuotas", cuotas);
                request.setAttribute("res_tipo", tipo);        // Para que sepa qué eligió
                request.setAttribute("res_tasa", tasaAnual);   // ¡Para mostrarle la tasa al cliente!
                request.setAttribute("res_valor_cuota", cuotaMensual);
                
                // IMPORTANTE: Volvemos al formulario (solicitar_prestamo.jsp)
                // NO al home, para evitar el error 500 que te salía antes.
                request.getRequestDispatcher("solicitar_prestamo.jsp").forward(request, response);

            } catch (Exception e) {
                response.sendRedirect("solicitar_prestamo.jsp?error=datos_invalidos");
            }
        }
        
        // ---------------------------------------------------------
        // CASO 2: REGISTRAR SOLICITUD (Guardar en BD)
        // ---------------------------------------------------------
// ---------------------------------------------------------
        // CASO 2: REGISTRAR SOLICITUD (Guardar y pedir documentos)
        // ---------------------------------------------------------
        else if ("solicitar".equals(accion)) {
            HttpSession session = request.getSession();
            
            // 1. VALIDAR SESIÓN (Usa el nombre correcto: "cliente")
            Cliente cliente = (Cliente) session.getAttribute("cliente");

            if (cliente != null) {
                try {
                    Solicitud s = new Solicitud();
                    s.setIdCliente(cliente.getId_Cliente());
                    s.setMonto(Double.parseDouble(request.getParameter("monto")));
                    s.setTipoCredito(request.getParameter("tipoCredito"));
                    s.setFechaSolicitud(new java.sql.Date(System.currentTimeMillis()));
                    // El estado se pone "PENDIENTE" automático en el constructor (Patrón State)

                    // 2. GUARDAR EN BD Y OBTENER ID
                    // (Llamamos al DAO directo para usar el método nuevo que devuelve int)
                    Dao.SolicitudDAO dao = new Dao.SolicitudDAO();
                    int idGenerado = dao.registrarSolicitudConRetorno(s);

                    if (idGenerado > 0) {
                        // 3. ÉXITO: Redirigir a Subir Documentos
                        // Importante: El nombre del atributo debe coincidir con el JSP
                        request.setAttribute("idSolicitud", idGenerado); 
                        request.getRequestDispatcher("subir_documentos.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("error.jsp");
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando solicitud: " + e.getMessage());
                    response.sendRedirect("error.jsp");
                }
            } else {
                // Si expiró la sesión
                response.sendRedirect("login_cliente.jsp");
            }
        }
        }
    }