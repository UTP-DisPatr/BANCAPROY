package Controladores;

import Modelo.Cliente;
import Patrones.ClienteFacade;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "PagarCuotaServlet", urlPatterns = {"/PagarCuotaServlet"})
public class PagarCuotaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Validar Sesión
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            try {
                // 2. Recibir datos del formulario (JSP)
                int idCuota = Integer.parseInt(request.getParameter("idCuota"));
                // El monto viene oculto en el formulario para saber cuánto descontar
                double monto = Double.parseDouble(request.getParameter("monto")); 

                // 3. Llamar al Facade
                ClienteFacade facade = new ClienteFacade();
                boolean pagoExitoso = facade.pagarCuota(cliente.getId_Cliente(), idCuota, monto);

                if (pagoExitoso) {
                    // 4. Si pagó, actualizamos el saldo en la sesión para que se vea reflejado YA
                    double nuevoSaldo = facade.consultarSaldo(cliente.getId_Cliente());
                    cliente.setSaldo(nuevoSaldo);
                    session.setAttribute("cliente", cliente); // Guardamos el cliente actualizado
                    
                    // Redirigir con mensaje de éxito
                    response.sendRedirect("HomeClienteServlet?msg=exito_pago");
                } else {
                    // Redirigir con error (seguramente saldo insuficiente)
                    response.sendRedirect("HomeClienteServlet?error=saldo_insuficiente");
                }

            } catch (Exception e) {
                System.out.println("Error en Servlet Pago: " + e.getMessage());
                response.sendRedirect("HomeClienteServlet?error=error_tecnico");
            }
        } else {
            response.sendRedirect("login_cliente.jsp");
        }
    }
}