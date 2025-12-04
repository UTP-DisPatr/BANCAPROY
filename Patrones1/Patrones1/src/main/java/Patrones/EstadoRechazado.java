package Patrones;
import Modelo.Solicitud;

public class EstadoRechazado implements EstadoSolicitud {
    @Override
    public void aprobar(Solicitud solicitud) {
        throw new IllegalStateException("No puedes rechazar un crédito ya rechazado.");
    }
    @Override
    public void rechazar(Solicitud solicitud) {
        throw new IllegalStateException("El crédito YA estaba rechazado.");
    }
    @Override
    public String getNombre() { return "RECHAZADO"; }
}