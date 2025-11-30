package Patrones;

import Dao.ClienteDAO;
import Modelo.Cliente;
import java.util.List;

public class ClienteFacade {

    private ClienteDAO dao = new ClienteDAO();

    public boolean registrarCliente(Cliente c) {
        return dao.insertar(c);
    }

    public List<Cliente> listarClientes() {
        return dao.listar();
    }
    public double consultarSaldo(int idCliente) {
        return dao.obtenerSaldo(idCliente);
    }
    public Cliente loginCliente(String dni, String password) {
        return dao.login(dni, password);
    }
    public java.util.List<Modelo.Cuota> obtenerCuotasPendientes(int idCliente) {
        return ((Dao.ClienteDAO)dao).listarCuotasPendientes(idCliente);
    }
}
