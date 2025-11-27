package Dao;

import Modelo.Cliente;
import Modelo.Bloque;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Patrones.ConexionBD;
import Patrones.Observador;

public class ClienteDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean insertar(Cliente c) {
    String sql = "INSERT INTO cliente (dni, nombre, apellido, email, telefono, password) "
               + "VALUES (?, ?, ?, ?, ?, ?)";

    try {
        con = ConexionBD.getConexion();
        ps = con.prepareStatement(sql);

        ps.setString(1, c.getDni());
        ps.setString(2, c.getNombre());
        ps.setString(3, c.getApellido());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getTelefono());
        ps.setString(6, c.getPassword());

        boolean insertado = ps.executeUpdate() > 0;

        if (insertado) {
            notificar(); // patrón Observer

            // ===== BLOKCHAIN AQUÍ =====
            BlockchainDAO bcDAO = new BlockchainDAO();
            String hashAnterior = bcDAO.obtenerUltimoHash();

            String datos = c.getDni() + "|" + c.getNombre() + "|" + c.getEmail();

            Bloque bloque = new Bloque(datos, hashAnterior);
            bcDAO.insertarBloque(bloque);
        }

        return insertado;

    } catch (Exception e) {
        System.out.println("Error insertar: " + e.getMessage());
        return false;
    }
}

private List<Observador> observadores = new ArrayList<>();

private void notificar() {
    for (Observador o : observadores) {
        o.actualizar("Nuevo cliente registrado");
    }
}

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try {
            con = ConexionBD.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId_cliente(rs.getInt("id_cliente"));
                c.setDni(rs.getString("dni"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setEmail(rs.getString("email"));
                c.setTelefono(rs.getString("telefono"));
                c.setPassword(rs.getString("password"));

                lista.add(c);
            }

        } catch (Exception e) {
            System.out.println("Error listar: " + e.getMessage());
        }

        return lista;
    }
}
