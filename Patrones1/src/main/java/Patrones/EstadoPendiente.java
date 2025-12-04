package Patrones;
import Modelo.Solicitud;

public class EstadoPendiente implements EstadoSolicitud {

    @Override
    public void aprobar(Solicitud solicitud) {
        solicitud.setEstadoObj(new EstadoAprobado());
        System.out.println("Solicitud Aprobada exitosamente.");
    }

    @Override
    public void rechazar(Solicitud solicitud) {
     
        solicitud.setEstadoObj(new EstadoRechazado());
        System.out.println("Solicitud Rechazada.");
    }

    @Override
    public String getNombre() {
        return "PENDIENTE"; 
}
}