package Dao;

import Modelo.Bloque;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import Patrones.ConexionBD;

public class BlockchainDAO {

    public void insertarBloque(Bloque b) {

    String datosParaHash = b.getDatos() + b.getHashAnterior() + System.currentTimeMillis();
    String hashNuevo = generarHash(datosParaHash);

    String sql = "INSERT INTO blockchain (datos, hash_actual, hash_anterior) VALUES (?, ?, ?)";

    try (Connection cn = ConexionBD.getConexion();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, b.getDatos());
        ps.setString(2, hashNuevo);
        ps.setString(3, b.getHashAnterior());
        ps.executeUpdate();

    } catch (Exception e) {
        System.out.println("Error blockchain: " + e.getMessage());
    }
}

    private String generarHash(String input) {
    try {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


    public String obtenerUltimoHash() {
        String hash = "0";

        String sql = "SELECT hash_actual FROM blockchain ORDER BY id_bloque DESC LIMIT 1";

        try (Connection cn = ConexionBD.getConexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                hash = rs.getString("hash_actual");
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo último hash: " + e.getMessage());
        }

        return hash;
    }
    public List<String> listarBloquesSimples() {
    List<String> lista = new java.util.ArrayList<>();
    String sql = "SELECT * FROM blockchain";

    try {
        Connection con = ConexionBD.getConexion();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String info =
                "ID: " + rs.getInt("id_bloque") +
                " | Hash: " + rs.getString("hash_actual") +
                " | Prev: " + rs.getString("hash_anterior") +
                " | Datos: " + rs.getString("datos");

            lista.add(info);
        }
    } catch (Exception e) {
        System.out.println("Error listar bloques: " + e.getMessage());
    }
    return lista;
}

}
