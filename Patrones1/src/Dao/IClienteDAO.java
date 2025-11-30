package Dao;
import Modelo.Cliente;
import Modelo.Cuota;
import java.util.List;

public interface IClienteDAO {
    // Otros métodos...
    boolean insertar(Cliente c);
    
    // CAMBIO AQUÍ: Validamos por DNI
    Cliente login(String dni, String password);
    
    double obtenerSaldo(int idCliente);
    List<Cuota> listarCuotasPendientes(int idCliente);
}