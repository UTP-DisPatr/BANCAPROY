package Patrones;
import Modelo.Solicitud;

public class EstadoAprobado implements EstadoSolicitud {
    @Override
    public void aprobar(Solicitud solicitud) {
        throw new IllegalStateException("El crédito YA estaba aprobado.");
    }
    @Override
    public void rechazar(Solicitud solicitud) {
        throw new IllegalStateException("No puedes rechazar un crédito ya aprobado.");
    }
    @Override
    public String getNombre() { return "APROBADO"; }
}