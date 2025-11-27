package Test;

import Patrones.ConexionBD;
import Patrones.ConexionBD;
import java.sql.Connection;

public class TestConexion {

    public static void main(String[] args) {
        try (Connection con = ConexionBD.getConexion()) {
            if (con != null && !con.isClosed()) {
                System.out.println("CONEXION EXITOSA A MYSQL");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
