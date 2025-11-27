package Patrones;

import Dao.ClienteDAO;
import Modelo.Cliente;

public class ClienteDAOProxy {

    private ClienteDAO dao = new ClienteDAO();

    public boolean insertar(Cliente c) {
        if (c.getNombre() == null || c.getNombre().isEmpty()) {
            return false;
        }
        return dao.insertar(c);
    }
}
