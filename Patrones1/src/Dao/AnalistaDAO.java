package Dao;

import Modelo.Analista;
import Patrones.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AnalistaDAO implements IAnalistaDAO {

    @Override 
    public Analista login(String usuario, String password) { 
        Analista analista = null;
        String sql = "SELECT * FROM analista WHERE usuario = ? AND password = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    analista = new Analista();
                    analista.setIdAnalista(rs.getInt("id_analista"));
                    analista.setNombre(rs.getString("nombre"));
                    analista.setUsuario(rs.getString("usuario"));
                    analista.setRol(rs.getString("rol"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error Login Analista: " + e.getMessage());
        }
        return analista;
    }
}