package Patrones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static Connection conexion;

    private static final String URL = "jdbc:mysql://localhost:3306/sistema_creditos";
    private static final String USER = "root";
    private static final String PASS = "";

    private ConexionBD() {} // Constructor privado

public static Connection getConexion() throws SQLException {
    if (conexion == null || conexion.isClosed()) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            conexion = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No encontré el Driver de BD");
        }
    }
    return conexion;
}
    }
