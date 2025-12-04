package Controladores;

import Modelo.Cliente;
import Patrones.ClienteFacade;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "LoginClienteServlet", urlPatterns = {"/LoginClienteServlet"})
public class LoginClienteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recibir DNI y Password (OJO: name="dni" en el HTML)
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");

        // 2. Usar el Facade para validar
        ClienteFacade facade = new ClienteFacade();
        Cliente cliente = facade.loginCliente(dni, password);

        if (cliente != null) {
            // LOGIN ÉXITO
            HttpSession session = request.getSession();
            session.setAttribute("cliente", cliente); 
            
            // Redirigimos al Home del Cliente
            response.sendRedirect("HomeClienteServlet"); 
        } else {
            // LOGIN FALLO
            response.sendRedirect("login_cliente.jsp?error=true");
        }
    }
}