import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ProcesarSolicitudServlet")
public class ProcesarSolicitudServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String idSolicitud = request.getParameter("id");
        String accion = request.getParameter("accion"); 

        String url = "jdbc:mysql://localhost:3306/sistema_creditos"; 
        String usuario = "root";
        String clave = ""; 

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, usuario, clave);
            con.setAutoCommit(false);

            if ("rechazar".equals(accion)) {
                ps = con.prepareStatement("UPDATE solicitudes_credito SET estado = 'RECHAZADO' WHERE id_solicitud = ?");
                ps.setString(1, idSolicitud);
                ps.executeUpdate();
                
            } else if ("aprobar".equals(accion)) {

                // PASO 1: Obtener cuánto dinero es y de qué cliente
                String sqlInfo = "SELECT monto, id_cliente FROM solicitudes_credito WHERE id_solicitud = ?";
                ps = con.prepareStatement(sqlInfo);
                ps.setString(1, idSolicitud);
                rs = ps.executeQuery();
       

                if (rs.next()) {
                    double montoPrestamo = rs.getDouble("monto");
                    int idCliente = rs.getInt("id_cliente");

                    // 1. Actualizar estado
                    ps = con.prepareStatement("UPDATE solicitudes_credito SET estado = 'APROBADO' WHERE id_solicitud = ?");
                    ps.setString(1, idSolicitud);
                    ps.executeUpdate();

                    // 2. Sumar saldo 
                    ps = con.prepareStatement("UPDATE cliente SET saldo = saldo + ? WHERE id_cliente = ?");
                    ps.setDouble(1, montoPrestamo);
                    ps.setInt(2, idCliente);
                    ps.executeUpdate();

                    // 3. GENERAR LAS CUOTAS (Ejemplo: 12 meses) ---
                    double montoCuota = montoPrestamo / 12; 

                    String sqlCuota = "INSERT INTO cuotas (id_solicitud, numero_cuota, monto, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, 'PENDIENTE')";
                    ps = con.prepareStatement(sqlCuota);

                    java.util.Calendar cal = java.util.Calendar.getInstance();

                    for (int i = 1; i <= 12; i++) {
                        cal.add(java.util.Calendar.MONTH, 1);
                        java.sql.Date fechaVenc = new java.sql.Date(cal.getTimeInMillis());

                        ps.setString(1, idSolicitud);
                        ps.setInt(2, i);           
                        ps.setDouble(3, montoCuota);
                        ps.setDate(4, fechaVenc);

                        ps.executeUpdate();
                    }
                }
            }

            con.commit();
            response.sendRedirect("bandeja_entrada.jsp?exito=si");

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) {}
        }
    }
}