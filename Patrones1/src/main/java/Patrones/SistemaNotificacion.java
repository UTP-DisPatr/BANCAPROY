package Patrones;



/**
 *
 * @author JHEINS
 */
public class SistemaNotificacion implements Observador {
    
    
// ESTA ES LA CLAVE: Una variable estática donde el Observer deja el mensaje.
    // Al ser estática, sobrevive entre clases.
    public static String buzonMensajes = ""; 

    @Override
    public void actualizar(String mensaje) {
        // 1. Imprime en consola (para debug del programador)
        System.out.println(">>> [OBSERVER] Notificación Generada: " + mensaje);
        
        // 2. Guarda el mensaje en el buzón para que el Servlet lo vea
        buzonMensajes = mensaje;
    }
}