package Patrones;

import Dao.ClienteDAO;
import Dao.IClienteDAO; // Importamos la Interfaz
import Dao.SolicitudDAO; // Importamos el DAO de Solicitudes
import Modelo.Cliente;
import Modelo.Cuota;
import Modelo.Solicitud;
import java.util.List;

public class ClienteFacade {

    // 1. CLAVE: Usamos la Interfaz pero instanciamos el PROXY
    // Así obligamos a que todo pase por la seguridad del Proxy
    private IClienteDAO dao = new ClienteDAOProxy();

    // --- MÉTODOS DE CLIENTE (Usan el Proxy) ---

    public boolean registrarCliente(Cliente c) {
        return dao.insertar(c);
    }

    public Cliente loginCliente(String dni, String password) {
        return dao.login(dni, password);
    }
    
    public double consultarSaldo(int idCliente) {
        return dao.obtenerSaldo(idCliente);
    }

    public List<Cuota> obtenerCuotasPendientes(int idCliente) {
        return dao.listarCuotasPendientes(idCliente);
    }

    // --- NUEVO: MÉTODO PARA PAGAR CUOTA (Usa el Proxy) ---
    // Este dará error rojo hasta que hagamos el Paso 2 (Actualizar Interface)
    public boolean pagarCuota(int idCliente, int idCuota, double monto) {
        return dao.pagarCuota(idCliente, idCuota, monto);
    }

    // --- MÉTODOS DE SOLICITUDES (Van directo al DAO de Solicitud) ---
    // El Facade actúa como "ventanilla única" para el Servlet
    
    public boolean registrarSolicitud(Solicitud s) {
        SolicitudDAO solicitudDao = new SolicitudDAO();
        return solicitudDao.registrarSolicitud(s);
    }
    // Archivo: Patrones/ClienteFacade.java

    // Agrega esto debajo de pagarCuota:
    public boolean realizarTransaccion(int idCliente, double monto, String tipo) {
        // Nota: Aquí estamos instanciando el ClienteDAO real para evitar modificar las interfaces/proxies
        Dao.ClienteDAO realDao = new Dao.ClienteDAO(); 
        return realDao.actualizarSaldo(idCliente, monto, tipo);
    }
}