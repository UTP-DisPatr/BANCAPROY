package Dao;

import Modelo.Cliente;
import Modelo.Bloque;
import Modelo.Cuota; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Patrones.ConexionBD;
import Patrones.Observador;

public class ClienteDAO implements IClienteDAO { 

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    private List<Observador> observadores = new ArrayList<>();

    // MÉTODO 1: INSERTAR (CON BLOCKCHAIN Y OBSERVER)
    @Override
    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO cliente (dni, nombre, apellido, email, telefono, password, saldo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)"; 

        try {
            con = ConexionBD.getConexion();
            ps = con.prepareStatement(sql);

            ps.setString(1, c.getDni());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getTelefono());
            ps.setString(6, c.getPassword());
            ps.setDouble(7, 0.00); 

            boolean insertado = ps.executeUpdate() > 0;

            if (insertado) {
                notificar(); 

                // BLOCKCHAIN
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

    private void notificar() {
        for (Observador o : observadores) {
            o.actualizar("Nuevo cliente registrado");
        }
    }

    // MÉTODO 2: LOGIN 
@Override
    public Cliente login(String dni, String password) { 
        Cliente c = null;
        String sql = "SELECT * FROM cliente WHERE dni = ? AND password = ?";

        try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni); 
            ps.setString(2, password);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Modelo.Cliente();
                    c.setId_Cliente(rs.getInt("id_cliente"));
                    c.setDni(rs.getString("dni"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellido(rs.getString("apellido"));
                    c.setEmail(rs.getString("email"));
                    // Recuperar saldo (importante para el Home)
                    try { c.setSaldo(rs.getDouble("saldo")); } catch(Exception ex){}
                }
            }
        } catch (Exception e) {
            System.out.println("Error Login DNI: " + e.getMessage());
        }
        return c;
    }

    // MÉTODO 3: LISTAR TODOS
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try {
            con = ConexionBD.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId_Cliente(rs.getInt("id_cliente"));
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


    // 1. Obtener Saldo Actual
    public double obtenerSaldo(int idCliente) {
        double saldo = 0;
        String sql = "SELECT saldo FROM cliente WHERE id_cliente = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getDouble("saldo");
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo saldo: " + e.getMessage());
        }
        return saldo;
    }

    // 2. Listar Cuotas Pendientes 

public List<Cuota> listarCuotasPendientes(int idCliente) {
    List<Cuota> lista = new ArrayList<>();
    
    String sql = "SELECT c.id_cuota, c.numero_cuota, c.monto_cuota, c.fecha_vencimiento, c.estado " +
                 "FROM cuotas c " +
                 "INNER JOIN solicitudes_credito s ON c.id_solicitud = s.id_solicitud " + 
                 "WHERE s.id_cliente = ? AND c.estado = 'PENDIENTE'";

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idCliente);
        
        try (ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                Cuota cuota = new Cuota();
                
                cuota.setIdCuota(rs.getInt("id_cuota")); 
                
                cuota.setNumeroCuota(rs.getInt("numero_cuota"));
                cuota.setMonto(rs.getDouble("monto_cuota"));
                cuota.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                cuota.setEstado(rs.getString("estado"));
                
                lista.add(cuota);
            }
        }
    } catch (Exception e) {
        System.out.println("Error listando cuotas: " + e.getMessage());
        e.printStackTrace();
    }
    return lista;
}
    //  Logica para pagar la cuota de deuda
    @Override
    public boolean pagarCuota(int idCliente, int idCuota, double monto) {
    boolean exito = false;
    // 1. Verificar saldo suficiente
    double saldoActual = obtenerSaldo(idCliente);
    if (saldoActual < monto) return false;

    String sqlDescuento = "UPDATE cliente SET saldo = saldo - ? WHERE id_cliente = ?";
    String sqlUpdateCuota = "UPDATE cuotas SET estado = 'PAGADO' WHERE id_cuota = ?";
    
    Connection con = null;
    try {
        con = ConexionBD.getConexion();
        con.setAutoCommit(false); 

        // Paso 1: Restar Saldo
        try (PreparedStatement ps1 = con.prepareStatement(sqlDescuento)) {
            ps1.setDouble(1, monto);
            ps1.setInt(2, idCliente);
            ps1.executeUpdate();
        }

        // Paso 2: Marcar cuota como Pagada
        try (PreparedStatement ps2 = con.prepareStatement(sqlUpdateCuota)) {
            ps2.setInt(1, idCuota);
            ps2.executeUpdate();
        }

        con.commit(); 
        exito = true;
        
    } catch (Exception e) {
        try { if(con!=null) con.rollback(); } catch(SQLException ex){} 
        System.out.println("Error al pagar: " + e.getMessage());
    } finally {
        try { if(con!=null) con.setAutoCommit(true); } catch(SQLException ex){}
    }
    return exito;
}

    public boolean actualizarSaldo(int idCliente, double monto, String tipo) {
        String sql;

        // 1. Lógica de Retiro (Verificación de Saldo)
        if ("RETIRO".equals(tipo)) {
            double saldoActual = obtenerSaldo(idCliente); 
            if (saldoActual < monto) return false; 
            sql = "UPDATE cliente SET saldo = saldo - ? WHERE id_cliente = ?";
        } 
        // 2. Lógica de Depósito (Suma)
        else if ("DEPOSITO".equals(tipo)) {
            sql = "UPDATE cliente SET saldo = saldo + ? WHERE id_cliente = ?";
        } else {
            return false;
        }

        try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, monto);
            ps.setInt(2, idCliente);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error actualizar saldo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
}
}