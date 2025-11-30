package Patrones;
import Modelo.Solicitud;

public interface EstadoSolicitud {
    void aprobar(Solicitud solicitud);
    void rechazar(Solicitud solicitud);
    String getNombre(); // Para guardar en BD ("PENDIENTE", "APROBADO")
}