// Archivo: Controladores/TransaccionServlet.java

package Controladores;

import Modelo.Cliente;
import Patrones.ClienteFacade;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "TransaccionServlet", urlPatterns = {"/TransaccionServlet"})
public class TransaccionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("transaccion_simple.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        if (cliente == null) {
            response.sendRedirect("login_cliente.jsp");
            return;
        }

        String tipoAccion = request.getParameter("accion"); 
        String montoStr = request.getParameter("monto");
        double monto = 0;
        
        try {
            monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                response.sendRedirect("TransaccionServlet?tipo=" + tipoAccion + "&error=monto_invalido");
                return;
            }
            
            ClienteFacade facade = new ClienteFacade();
            boolean exito = facade.realizarTransaccion(cliente.getId_Cliente(), monto, tipoAccion);
            
            if (exito) {
                double nuevoSaldo = facade.consultarSaldo(cliente.getId_Cliente());
                cliente.setSaldo(nuevoSaldo);
                session.setAttribute("cliente", cliente);
                
                response.sendRedirect("HomeClienteServlet?msg=transaccion_ok");
            } else {
                response.sendRedirect("TransaccionServlet?tipo=" + tipoAccion + "&error=saldo_insuficiente");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("TransaccionServlet?tipo=" + tipoAccion + "&error=monto_invalido");
        } catch (Exception e) {
            System.out.println("Error en TransaccionServlet: " + e.getMessage());
            response.sendRedirect("TransaccionServlet?tipo=" + tipoAccion + "&error=tecnico");
        }
    }
}