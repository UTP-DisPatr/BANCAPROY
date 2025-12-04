package Dao;
import Modelo.Cliente;
import Modelo.Cuota;
import java.util.List;

public interface IClienteDAO {
    // Otros métodos...
    boolean insertar(Cliente c);
    boolean pagarCuota(int idCliente, int idCuota, double monto);
    // CAMBIO AQUÍ: Validamos por DNI
    Cliente login(String dni, String password);
    
    double obtenerSaldo(int idCliente);
    List<Cuota> listarCuotasPendientes(int idCliente);
    
}