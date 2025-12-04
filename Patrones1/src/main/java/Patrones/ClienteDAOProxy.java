package Patrones;

import Dao.ClienteDAO;
import Dao.IClienteDAO; 
import Modelo.Cliente;
import Modelo.Cuota;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

// 1. Agregar "implements IClienteDAO
public class ClienteDAOProxy implements IClienteDAO {

    // 2. Le ponemos "daoReal" para que coincida con lo de abajo
    private ClienteDAO daoReal = new ClienteDAO();

    @Override
    public boolean insertar(Cliente c) {
        if (c.getNombre() == null || c.getNombre().isEmpty()) {
            return false;
        }
        return daoReal.insertar(c);
    }

@Override
public boolean pagarCuota(int idCliente, int idCuota, double monto) {
    System.out.println("PROXY: Iniciando transacción de pago para cuota ID: " + idCuota);
    return daoReal.pagarCuota(idCliente, idCuota, monto);
}
    @Override
    public Cliente login(String dni, String password) {
        System.out.println("PROXY: Verificando DNI: " + dni);

        if (dni == null || password == null) return null;

       
        return daoReal.login(dni, password);
    }


    @Override
    public double obtenerSaldo(int idCliente) {
        return daoReal.obtenerSaldo(idCliente);
    }

    @Override
    public List<Cuota> listarCuotasPendientes(int idCliente) {
        return daoReal.listarCuotasPendientes(idCliente);
    }
    
}