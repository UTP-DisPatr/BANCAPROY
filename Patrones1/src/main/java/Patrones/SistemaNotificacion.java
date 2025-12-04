package Patrones;



/**
 *
 * @author JHEINS
 */
public class SistemaNotificacion implements Observador {
    
    
    public static String buzonMensajes = ""; 

    @Override
    public void actualizar(String mensaje) {
        System.out.println(">>> [OBSERVER] Notificación Generada: " + mensaje);
        
        buzonMensajes = mensaje;
    }
}