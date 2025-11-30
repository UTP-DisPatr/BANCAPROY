package Patrones;



/**
 *
 * @author JHEINS
 */
public class SistemaNotificacion implements Observador {
    public void actualizar(String m) {
        System.out.println("Notificación: " + m);
    }
}
