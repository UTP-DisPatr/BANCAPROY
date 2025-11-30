package Patrones;

import Dao.ClienteDAO;
import Dao.IClienteDAO; // <--- IMPORTANTE
import Modelo.Cliente;
import Modelo.Cuota;
import java.util.List;

// 1. CORRECCIÓN: Agregar "implements IClienteDAO"
public class ClienteDAOProxy implements IClienteDAO {

    // 2. CORRECCIÓN: Le ponemos "daoReal" para que coincida con lo de abajo
    private ClienteDAO daoReal = new ClienteDAO();

    @Override
    public boolean insertar(Cliente c) {
        if (c.getNombre() == null || c.getNombre().isEmpty()) {
            return false;
        }
        return daoReal.insertar(c);
    }

    @Override
    public Cliente login(String dni, String password) {
        System.out.println("PROXY: Verificando DNI: " + dni);

        if (dni == null || password == null) return null;

        // Aquí usamos la variable "daoReal" que definimos arriba
        return daoReal.login(dni, password);
    }

    // --- MÉTODOS OBLIGATORIOS QUE TE FALTAN (Porque están en la Interfaz) ---

    @Override
    public double obtenerSaldo(int idCliente) {
        return daoReal.obtenerSaldo(idCliente);
    }

    @Override
    public List<Cuota> listarCuotasPendientes(int idCliente) {
        return daoReal.listarCuotasPendientes(idCliente);
    }
}