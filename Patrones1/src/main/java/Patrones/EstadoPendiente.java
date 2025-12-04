package Patrones;
import Modelo.Solicitud;

public class EstadoPendiente implements EstadoSolicitud {

    @Override
    public void aprobar(Solicitud solicitud) {
        // Regla de Negocio: De Pendiente SÍ puede pasar a Aprobado
        solicitud.setEstadoObj(new EstadoAprobado());
        System.out.println("Solicitud Aprobada exitosamente.");
    }

    @Override
    public void rechazar(Solicitud solicitud) {
        // Regla de Negocio: De Pendiente SÍ puede pasar a Rechazado
        solicitud.setEstadoObj(new EstadoRechazado());
        System.out.println("Solicitud Rechazada.");
    }

    @Override
    public String getNombre() {
        return "PENDIENTE"; 
}
}