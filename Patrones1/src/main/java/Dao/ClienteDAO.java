package Dao;

import Modelo.Cliente;
import Modelo.Bloque;
import Modelo.Cuota; // <--- IMPORTANTE: Agregamos esto para las listas de pagos
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Patrones.ConexionBD;
import Patrones.Observador;

// Asegúrate de que implemente la interfaz si la estás usando en el Facade/Proxy
public class ClienteDAO implements IClienteDAO { 

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    private List<Observador> observadores = new ArrayList<>();

    // ---------------------------------------------------
    // MÉTODO 1: INSERTAR (CON BLOCKCHAIN Y OBSERVER)
    // ---------------------------------------------------
    @Override // Si usas la interfaz
    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO cliente (dni, nombre, apellido, email, telefono, password, saldo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)"; // Agregué SALDO al insert por si acaso (default 0)

        try {
            con = ConexionBD.getConexion();
            ps = con.prepareStatement(sql);

            ps.setString(1, c.getDni());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getTelefono());
            ps.setString(6, c.getPassword());
            ps.setDouble(7, 0.00); // Saldo inicial 0

            boolean insertado = ps.executeUpdate() > 0;

            if (insertado) {
                notificar(); // patrón Observer

                // ===== BLOCKCHAIN AQUÍ =====
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

    // ---------------------------------------------------
    // MÉTODO 2: LOGIN (Recuperado de pasos anteriores)
    // ---------------------------------------------------
@Override
    public Cliente login(String dni, String password) { // Recibe DNI
        Cliente c = null;
        // SQL CORREGIDO: WHERE dni = ?
        String sql = "SELECT * FROM cliente WHERE dni = ? AND password = ?";

        try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni); // Insertamos el DNI
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

    // ---------------------------------------------------
    // MÉTODO 3: LISTAR TODOS
    // ---------------------------------------------------
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

    // ====================================================================
    //  NUEVOS MÉTODOS PARA EL HOME BANKING (PASO 3 INTEGRADO)
    // ====================================================================

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

    // 2. Listar Cuotas Pendientes (JOIN: Cliente -> Solicitud -> Credito -> Cuota)
    public List<Cuota> listarCuotasPendientes(int idCliente) {
        List<Cuota> lista = new ArrayList<>();
        
        // Consulta poderosa: Busca las cuotas de los créditos asociados al cliente
        String sql = "SELECT c.id_cuota, c.numero_cuota, c.monto_cuota, c.fecha_vencimiento, c.estado " +
                     "FROM cuotas c " +
                     "INNER JOIN creditos cr ON c.id_credito = cr.id_credito " +
                     "INNER JOIN solicitudes_credito s ON cr.id_solicitud = s.id_solicitud " +
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
        }
        return lista;
    }
    // ====================================================================
    //  Logica para pagar la cuota de deuda
    // ====================================================================
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
        con.setAutoCommit(false); // INICIO TRANSACCIÓN

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

        con.commit(); // CONFIRMAR TRANSACCIÓN
        exito = true;
        
    } catch (Exception e) {
        try { if(con!=null) con.rollback(); } catch(SQLException ex){} // Deshacer si falla
        System.out.println("Error al pagar: " + e.getMessage());
    } finally {
        try { if(con!=null) con.setAutoCommit(true); } catch(SQLException ex){}
    }
    return exito;
}
}