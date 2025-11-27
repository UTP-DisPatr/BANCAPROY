package Controladores;
import Modelo.Cliente;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import Patrones.ClienteFacade;

// ...imports...

@WebServlet("/ClienteServlet") // Asegúrate que esta ruta coincide con el form action
public class ClienteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recibir datos
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String password = request.getParameter("password");

        Cliente c = new Cliente(); 
        c.setDni(dni);
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setEmail(email);
        c.setTelefono(telefono);
        c.setPassword(password);

        ClienteFacade facade = new ClienteFacade();

        boolean resultado = facade.registrarCliente(c);

        if (resultado) {
            response.sendRedirect("exito.jsp");
        } else {
            response.sendRedirect("error.jsp");
        }
    }
}
