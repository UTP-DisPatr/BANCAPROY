
package Test;

/**
 *
 * @author JHEINS
 */

import Dao.ClienteDAO;
import Modelo.Cuota;
import java.util.List;



public class TestCuotas {
    public static void main(String[] args) {
        // 1. Pon aquí manualmente el ID del cliente que tiene la deuda en tu BD
        int ID_CLIENTE_A_PROBAR = 2; 

        System.out.println("=== INICIO TEST DE CUOTAS ===");
        
        ClienteDAO dao = new ClienteDAO();
        try {
            List<Cuota> cuotas = dao.listarCuotasPendientes(ID_CLIENTE_A_PROBAR);
            
            if (cuotas.isEmpty()) {
                System.out.println("RESULTADO: La lista está vacía.");
                System.out.println("POSIBLES CAUSAS:");
                System.out.println("1. Ese ID de cliente no tiene créditos.");
                System.out.println("2. Las cuotas no tienen estado 'PENDIENTE'.");
                System.out.println("3. Falló la conexión (revisar logs arriba).");
            } else {
                System.out.println("¡ÉXITO! Se encontraron " + cuotas.size() + " cuotas.");
                for (Cuota c : cuotas) {
                    System.out.println("- Cuota " + c.getNumeroCuota() + " | Monto: " + c.getMonto() + " | Estado: " + c.getEstado());
                }
            }
        } catch (Exception e) {
            System.out.println("!!! ERROR GRAVE !!!");
            e.printStackTrace();
        }
        System.out.println("=== FIN TEST ===");
    }
}