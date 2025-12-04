package Patrones;

import Dao.ClienteDAO;
import Dao.IClienteDAO; 
import Dao.SolicitudDAO; 
import Modelo.Cliente;
import Modelo.Cuota;
import Modelo.Solicitud;
import java.util.List;

public class ClienteFacade {

    // 1. Usamos la Interfaz pero instanciamos el PROXY
    private IClienteDAO dao = new ClienteDAOProxy();


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

    public boolean pagarCuota(int idCliente, int idCuota, double monto) {
        return dao.pagarCuota(idCliente, idCuota, monto);
    }

    
    public boolean registrarSolicitud(Solicitud s) {
        SolicitudDAO solicitudDao = new SolicitudDAO();
        return solicitudDao.registrarSolicitud(s);
    }
    public boolean realizarTransaccion(int idCliente, double monto, String tipo) {
        Dao.ClienteDAO realDao = new Dao.ClienteDAO(); 
        return realDao.actualizarSaldo(idCliente, monto, tipo);
    }
}