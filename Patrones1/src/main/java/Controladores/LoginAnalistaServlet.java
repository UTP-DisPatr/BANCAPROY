package Controladores;

import Modelo.Analista;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Patrones.AnalistaFacade;

@WebServlet("/LoginAnalistaServlet")
public class LoginAnalistaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("password");

        AnalistaFacade facade = new AnalistaFacade();
        Analista analista = facade.iniciarSesion(usuario, clave);

        if (analista != null) {
            // LOGIN EXITOSO: Guardamos al analista en la SESIÓN
            HttpSession session = request.getSession();
            session.setAttribute("analistaLogueado", analista);
            
            // Redirigimos a la bandeja de entrada
            response.sendRedirect("bandeja_entrada.jsp");
        } else {
            // LOGIN FALLIDO
            response.sendRedirect("login_analista.jsp?error=true");
        }
    }
}