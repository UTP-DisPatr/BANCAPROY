package Patrones;
import Modelo.Solicitud;

public class EstadoPendiente implements EstadoSolicitud {
    @Override
    public void aprobar(Solicitud solicitud) {
        // Lógica: De Pendiente pasa a Aprobado
        solicitud.setEstadoObj(new EstadoAprobado());
        System.out.println("Solicitud aprobada.");
    }

    @Override
    public void rechazar(Solicitud solicitud) {
        // Lógica: De Pendiente pasa a Rechazado
        solicitud.setEstadoObj(new EstadoRechazado());
        System.out.println("Solicitud rechazada.");
    }

    @Override
    public String getNombre() { return "PENDIENTE"; }
}