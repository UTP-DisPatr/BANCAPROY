package Dao;

import Modelo.Solicitud;
import Patrones.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

        public List<Solicitud> listarPendientes() {
        List<Solicitud> lista = new ArrayList<>();
        
        
        String sql = "SELECT s.id_solicitud, s.id_cliente, s.monto, s.tipo_credito, " +
                     "s.estado, s.fecha_solicitud, c.nombre AS nombre_cliente " +
                     "FROM solicitudes_credito s " +  
                     "INNER JOIN cliente c ON s.id_cliente = c.id_cliente " + 
                     "WHERE s.estado = 'PENDIENTE'";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Solicitud s = new Solicitud();
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setIdCliente(rs.getInt("id_cliente"));
                s.setMonto(rs.getDouble("monto"));
                s.setTipoCredito(rs.getString("tipo_credito"));
                s.setEstado(rs.getString("estado"));
                s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                
                s.setNombreCliente(rs.getString("nombre_cliente"));
                
                lista.add(s);
            }
        } catch (Exception e) {
            System.out.println("Error listar solicitudes: " + e.getMessage());
        }
        return lista;
    }
    
    // Método para que el Cliente registre la solicitud 
    public boolean registrarSolicitud(Solicitud s) {
        String sql = "INSERT INTO solicitudes_credito (id_cliente, monto, tipo_credito, estado, fecha_solicitud) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, s.getIdCliente());
            ps.setDouble(2, s.getMonto());
            ps.setString(3, s.getTipoCredito());
            ps.setString(4, s.getEstado()); 
            ps.setDate(5, s.getFechaSolicitud());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error registrando solicitud: " + e.getMessage());
            return false;
        }
    }

    public Solicitud obtenerPorId(int id) {
        Solicitud s = null;
        
        String sql = "SELECT s.*, c.nombre, c.apellido " +
                     "FROM solicitudes_credito s " +
                     "INNER JOIN cliente c ON s.id_cliente = c.id_cliente " +
                     "WHERE s.id_solicitud = ?";
        
        try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s = new Solicitud();
                    s.setIdSolicitud(rs.getInt("id_solicitud"));
                    s.setIdCliente(rs.getInt("id_cliente"));
                    s.setMonto(rs.getDouble("monto"));
                    s.setTipoCredito(rs.getString("tipo_credito"));
                    s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                    s.setEstado(rs.getString("estado")); 
                    
                    String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                    s.setNombreCliente(nombreCompleto);
                }
            }
        } catch (Exception e) {
            System.out.println("Error obtenerPorId: " + e.getMessage());
        }
        return s;
    }

    //  Método para guardar la Aprobación/Rechazo del Analista 
    public boolean actualizarEstado(Solicitud s) {
        String sql = "UPDATE solicitudes_credito SET estado = ?, id_analista = ? WHERE id_solicitud = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, s.getEstado()); 
            ps.setInt(2, s.getIdAnalista());
            ps.setInt(3, s.getIdSolicitud());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error actualizando estado: " + e.getMessage());
            return false;
        }
    }


public int registrarSolicitudConRetorno(Modelo.Solicitud s) {
    
    String sql = "INSERT INTO solicitudes_credito (id_cliente, monto, tipo_credito, estado, fecha_solicitud) VALUES (?, ?, ?, ?, ?)";
    int idGenerado = 0;

    try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, s.getIdCliente());
        ps.setDouble(2, s.getMonto());
        ps.setString(3, s.getTipoCredito());
        ps.setString(4, s.getEstadoString()); 
        ps.setDate(5, s.getFechaSolicitud());
        

        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }
        }
    } catch (Exception e) {
        System.out.println("--- ERROR SQL FATAL AL REGISTRAR SOLICITUD ---");
        System.out.println("Mensaje de MySQL: " + e.getMessage());
        e.printStackTrace(); 
    }
    return idGenerado;
}


public boolean aprobarCredito(int idSolicitud, int idCliente, double monto, int numCuotas, int idAnalista) {
    boolean exito = false;
    java.sql.Connection con = null;
    
    // 1. Actualizar estado de la solicitud
    String sqlEstado = "UPDATE solicitudes_credito SET estado = 'APROBADO', id_analista = ? WHERE id_solicitud = ?";
    
    // 2. Dar dinero al cliente
    String sqlSaldo = "UPDATE cliente SET saldo = saldo + ? WHERE id_cliente = ?";  
    
    // 3. Crear Cuotas
    String sqlCuota = "INSERT INTO cuotas (id_solicitud, numero_cuota, monto_cuota, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, 'PENDIENTE')";

    try {
        con = Patrones.ConexionBD.getConexion();
        con.setAutoCommit(false); 

        // A. Cambiar Estado
        try (java.sql.PreparedStatement ps1 = con.prepareStatement(sqlEstado)) {
            ps1.setInt(1, idAnalista);
            ps1.setInt(2, idSolicitud);
            ps1.executeUpdate();
        }

        // B. Sumar Saldo
        try (java.sql.PreparedStatement ps2 = con.prepareStatement(sqlSaldo)) {
            ps2.setDouble(1, monto);
            ps2.setInt(2, idCliente);
            ps2.executeUpdate();
        }

        // C. Generar Cuotas
        double montoCuota = Math.round((monto / numCuotas) * 100.0) / 100.0;
        
        try (java.sql.PreparedStatement ps4 = con.prepareStatement(sqlCuota)) {
            for (int i = 1; i <= numCuotas; i++) {
                
                ps4.setInt(1, idSolicitud); 
                
                ps4.setInt(2, i);
                ps4.setDouble(3, montoCuota);
                
             
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.MONTH, i);
                ps4.setDate(4, new java.sql.Date(cal.getTimeInMillis()));
                
                ps4.executeUpdate();
            }
        }

        con.commit(); 
        exito = true;

    } catch (Exception e) {
        try { if(con!=null) con.rollback(); } catch(Exception ex){}
        System.out.println("Error al aprobar (Final): " + e.getMessage());
        e.printStackTrace();
    } finally {
        try { if(con!=null) con.setAutoCommit(true); } catch(Exception ex){}
    }
    return exito;
}

    // Método auxiliar para ver los nombres de archivos subidos
    public java.util.List<String> obtenerDocumentos(int idSolicitud) {
        java.util.List<String> docs = new java.util.ArrayList<>();
        String sql = "SELECT nombre_archivo FROM documentos_solicitud WHERE id_solicitud = ?";
        try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            java.sql.ResultSet rs = ps.executeQuery();
            while(rs.next()) docs.add(rs.getString("nombre_archivo"));
        } catch(Exception e) {}
        return docs;
    }
  
    public void registrarDocumento(int idSolicitud, String tipo, String nombreArchivo) {
        String sql = "INSERT INTO documentos_solicitud (id_solicitud, tipo_documento, nombre_archivo, ruta_archivo) VALUES (?, ?, ?, ?)";
        
        try (java.sql.Connection con = Patrones.ConexionBD.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idSolicitud);
            ps.setString(2, tipo);
            ps.setString(3, nombreArchivo);
            ps.setString(4, "C:/banco_uploads/" + nombreArchivo); 
            
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.out.println("Error guardando documento en BD: " + e.getMessage());
        }
    }
}