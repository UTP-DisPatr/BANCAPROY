package Test;

import Dao.BlockchainDAO;
import Modelo.Cliente;
import Patrones.ClienteFacade;
import Patrones.ClienteFactory;
import Patrones.ConexionBD;
import java.util.List;

public class MainTest {

public static void main(String[] args) throws Exception {

        System.out.println("===== PRUEBA COMPLETA DEL SISTEMA =====");

        // ----------------------------------------
        // 1. Probar Singleton (Conexión)
        // ----------------------------------------
        System.out.println("\n[1] Probando Singleton - ConexionBD");
        if (ConexionBD.getConexion() != null) {
            System.out.println("Conexión creada correctamente ✅");
        } else {
            System.out.println("Error en conexión ❌");
        }

        // ----------------------------------------
        // 2. Probar Factory
        // ----------------------------------------
        System.out.println("\n[2] Probando Factory");
        Cliente cliente = ClienteFactory.crearCliente(
                0,                   
                "12345678",
                "Juan",
                "Perez",
                "juan@email.com",
                "999999999",
                "1234"
);

        System.out.println("Cliente creado con Factory ✅");

        // ----------------------------------------
        // 3. Probar Facade + Proxy + DAO
        // ----------------------------------------
        System.out.println("\n[3] Insertando cliente usando Facade + Proxy");

        ClienteFacade facade = new ClienteFacade();
        boolean insertado = facade.registrarCliente(cliente);

        if (insertado) {
            System.out.println("Cliente registrado correctamente ✅");
        } else {
            System.out.println("No se pudo registrar ❌");
        }

        // ----------------------------------------
        // 4. Probar Listado de Clientes
        // ----------------------------------------
        System.out.println("\n[4] Listando clientes");

        List<Cliente> clientes = facade.listarClientes();
        for (Cliente c : clientes) {
            System.out.println(
                c.getId_cliente() + " | " +
                c.getDni() + " | " +
                c.getNombre() + " | " +
                c.getEmail()
            );
        }

        // ----------------------------------------
        // 5. Probar Blockchain
        // ----------------------------------------
        System.out.println("\n[5] Probando Blockchain");

        BlockchainDAO bcDAO = new BlockchainDAO();
        List<String> bloques = obtenerBloques(bcDAO);

        if (bloques.isEmpty()) {
            System.out.println("No hay bloques ❌");
        } else {
            System.out.println("Bloques registrados ✅");
            for (String b : bloques) {
                System.out.println(b);
            }
        }

        // ----------------------------------------
        // 6. Fin de pruebas
        // ----------------------------------------
        System.out.println("\n===== PRUEBAS FINALIZADAS =====");
    }

    // Método auxiliar SOLO para mostrar bloques
    private static List<String> obtenerBloques(BlockchainDAO dao) {
        return dao.listarBloquesSimples();
    }
}
