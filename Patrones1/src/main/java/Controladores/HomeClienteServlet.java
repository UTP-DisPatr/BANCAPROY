package Controladores;

import Modelo.Cliente;
import Modelo.Cuota;
import Patrones.ClienteFacade;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

// OJO: La URL debe ser EXACTAMENTE esta para que coincida con el error
@WebServlet(name = "HomeClienteServlet", urlPatterns = {"/HomeClienteServlet"})
public class HomeClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Sesión
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            response.sendRedirect("login_cliente.jsp");
            return;
        }

        // 2. Obtener Datos con Facade
        ClienteFacade facade = new ClienteFacade();
        double saldo = facade.consultarSaldo(cliente.getId_Cliente());
        List<Cuota> listaCuotas = facade.obtenerCuotasPendientes(cliente.getId_Cliente());

        // 3. Mandar a la vista
        request.setAttribute("saldoActual", saldo);
        request.setAttribute("listaCuotas", listaCuotas);
        
        request.getRequestDispatcher("home_cliente.jsp").forward(request, response);
    }

}