package Test;

import Dao.SolicitudDAO;
import Modelo.SimuladorCredito; 
import Modelo.Solicitud;
import Patrones.ConexionBD;
import java.sql.Connection;
import java.util.List; 

public class MainTest1 {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   INICIANDO PRUEBAS DEL SISTEMA BANCARIO");
        System.out.println("==========================================\n");

        // 1. PRUEBA DE CONEXIÓN BD
        System.out.println("--- TEST 1: Conexión a Base de Datos ---");
        try {
            Connection con = ConexionBD.getConexion();
            if(con != null && !con.isClosed()) {
                System.out.println("Conexión EXITOSA.");
            } else {
                System.out.println("ERROR: No hay conexión.");
            }
        } catch (Exception e) {
            System.out.println(" Excepción en conexión: " + e.getMessage());
        }
        System.out.println();

        // 2. PRUEBA DE SIMULACIÓN
        System.out.println("--- TEST 2: Simulador de Crédito ---");
        SimuladorCredito simulador = new SimuladorCredito();
        double monto = 10000;
        int meses = 12;
        double tasa = 15.0; 
        
        double cuota = simulador.calcularCuota(monto, meses, tasa);
        System.out.println("Monto: " + monto + " | Meses: " + meses + " | Tasa: " + tasa + "%");
        System.out.println("Cuota Calculada: " + cuota);
        
        if (cuota > 0) System.out.println(" Cálculo lógico.");
        else System.out.println(" Error en cálculo.");
        System.out.println();

        // 3. PRUEBA DEL PATRÓN STATE
        System.out.println("--- TEST 3: Patrón State (Flujo de Aprobación) ---");
        Solicitud solTest = new Solicitud();
        System.out.println("Estado Inicial: " + solTest.getEstadoString()); 
        
        System.out.println("Intentando aprobar...");
        solTest.aprobar(); 
        System.out.println("Estado Nuevo: " + solTest.getEstadoString()); 
        
        System.out.println("Intentando aprobar de nuevo (debe fallar)...");
        try {
            solTest.aprobar();
            System.out.println(" ERROR: El patrón NO protegió el estado.");
        } catch (IllegalStateException e) {
            System.out.println(" ÉXITO: El patrón bloqueó la acción: " + e.getMessage());
        }
        System.out.println();

        // 4. PRUEBA INTEGRAL (DAO + BD)
        System.out.println("--- TEST 4: Persistencia en BD (Crear y Actualizar) ---");
        SolicitudDAO dao = new SolicitudDAO();
        
        Solicitud nuevaSol = new Solicitud();
        nuevaSol.setIdCliente(1); 
        nuevaSol.setMonto(5000);
        nuevaSol.setTipoCredito("PERSONAL");
        nuevaSol.setFechaSolicitud(new java.sql.Date(System.currentTimeMillis()));
        
        if (dao.registrarSolicitud(nuevaSol)) {
            System.out.println("Solicitud insertada en BD correctamente.");
        } else {
            System.out.println("Falló el insert en BD (¿Existe el cliente 1?).");
            return; 
        }

        List<Solicitud> pendientes = dao.listarPendientes();
        
        if (pendientes.isEmpty()) {
            System.out.println(" No se encontraron pendientes para probar.");
        } else {
            Solicitud recuperada = pendientes.get(pendientes.size() - 1); 
            System.out.println("Solicitud recuperada ID: " + recuperada.getIdSolicitud());
            System.out.println("Estado recuperado de BD: " + recuperada.getEstadoString());

            recuperada.setIdAnalista(1); 
            recuperada.rechazar(); 
            
            if (dao.actualizarEstado(recuperada)) {
                System.out.println("✅ Estado actualizado en BD a: " + recuperada.getEstadoString());
            } else {
                System.out.println("❌ Falló la actualización en BD.");
            }
        }
        
        System.out.println("\n==========================================");
        System.out.println("          PRUEBAS FINALIZADAS");
        System.out.println("==========================================");
    }
}