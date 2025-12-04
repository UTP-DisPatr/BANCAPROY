package Dao;
import Modelo.Cliente;
import Modelo.Cuota;
import java.util.List;

public interface IClienteDAO {
    boolean insertar(Cliente c);
    boolean pagarCuota(int idCliente, int idCuota, double monto);
    Cliente login(String dni, String password);
    
    double obtenerSaldo(int idCliente);
    List<Cuota> listarCuotasPendientes(int idCliente);
    
}