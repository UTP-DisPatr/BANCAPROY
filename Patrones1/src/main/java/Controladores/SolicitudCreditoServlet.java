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
        
        String accion = request.getParameter("accion");
        

        // CASO 1: SIMULACIÓN DE CRÉDITO
        if ("simular".equals(accion)) {
            try {
                // 1. Recibimos datos
                double monto = Double.parseDouble(request.getParameter("monto"));
                int cuotas = Integer.parseInt(request.getParameter("cuotas"));
                
                String tipo = request.getParameter("tipoCredito"); 

                // 2. Lógica de Negocio
                SimuladorCredito simulador = new SimuladorCredito();
                
                double tasaAnual = simulador.obtenerTasaAnual(tipo);   
                double cuotaMensual = simulador.calcularCuota(monto, cuotas, tasaAnual);

                request.setAttribute("res_monto", monto);
                request.setAttribute("res_cuotas", cuotas);
                request.setAttribute("res_tipo", tipo);        
                request.setAttribute("res_tasa", tasaAnual);   
                request.setAttribute("res_valor_cuota", cuotaMensual);
                
                request.getRequestDispatcher("solicitar_prestamo.jsp").forward(request, response);

            } catch (Exception e) {
                response.sendRedirect("solicitar_prestamo.jsp?error=datos_invalidos");
            }
        }
        

// CASO 2: REGISTRAR SOLICITUD (Guardar y pedir documentos)
        
        else if ("solicitar".equals(accion)) {
            HttpSession session = request.getSession();
            
            // 1. VALIDAR SESIÓN
            Cliente cliente = (Cliente) session.getAttribute("cliente");

            if (cliente != null) {
                try {
                    Solicitud s = new Solicitud();
                    s.setIdCliente(cliente.getId_Cliente());
                    s.setMonto(Double.parseDouble(request.getParameter("monto")));
                    
                    // 1. Obtener el valor de cuotas del formulario
                    int cuotas = Integer.parseInt(request.getParameter("cuotas"));
                    // 2. Asignar el valor de cuotas al objeto Solicitud
                    s.setCuotas(cuotas); 
                    // -----------------------------------
                    
                    s.setTipoCredito(request.getParameter("tipoCredito"));
                    s.setFechaSolicitud(new java.sql.Date(System.currentTimeMillis()));

                    // 2. GUARDAR EN BD Y OBTENER ID
                    Dao.SolicitudDAO dao = new Dao.SolicitudDAO();
                    int idGenerado = dao.registrarSolicitudConRetorno(s);

                    if (idGenerado > 0) {
                        // 3. ÉXITO: Redirigir a Subir Documentos
                        request.setAttribute("idSolicitud", idGenerado); 
                        request.setAttribute("cuotasSolicitadas", s.getCuotas());
                        request.getRequestDispatcher("subir_documentos.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("error.jsp?error_msg=Fallo_al_registrar_en_BD");
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando solicitud: " + e.getMessage());
                    e.printStackTrace(); 
                    response.sendRedirect("error.jsp?error_msg=Excepcion_Tecnica");
                }
            } else {
                response.sendRedirect("login_cliente.jsp");
            }
        }
    }
}