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
}
