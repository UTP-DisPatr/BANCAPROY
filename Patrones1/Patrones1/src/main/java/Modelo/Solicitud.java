package Modelo;
import Patrones.*;
import java.sql.Date;

public class Solicitud {
    private int idSolicitud;
    private int idCliente;
    private int idAnalista;
    private double monto;
    private String tipoCredito; 
    private String estado;
    private Date fechaSolicitud; 
    private EstadoSolicitud estadoObj;
    private int cuotas;
    private String nombreCliente; 

    public Solicitud() {
        // Por defecto nace pendiente
        this.estadoObj = new EstadoPendiente();
    }
    
    public void aprobar() { this.estadoObj.aprobar(this); }
    public void rechazar() { this.estadoObj.rechazar(this); }
    
    public void setEstadoObj(EstadoSolicitud estado) {
        this.estadoObj = estado;
    }
    
    // --- PUENTE CON LA BASE DE DATOS ---
    // El DAO usará esto para guardar "PENDIENTE" o "APROBADO"
    public String getEstadoString() {
        return estadoObj.getNombre();
    }
    
    // El DAO usará esto al leer de la BD para restaurar el objeto
    public void setEstadoString(String estadoBD) {
        if (estadoBD.equalsIgnoreCase("APROBADO")) {
            this.estadoObj = new EstadoAprobado();
        } else if (estadoBD.equalsIgnoreCase("RECHAZADO")) {
            this.estadoObj = new EstadoRechazado();
        } else {
            this.estadoObj = new EstadoPendiente();
        }
    }

    // --- GETTERS Y SETTERS ---
    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdAnalista() { return idAnalista; }
    public void setIdAnalista(int idAnalista) { this.idAnalista = idAnalista; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipoCredito() { return tipoCredito; }
    public void setTipoCredito(String tipoCredito) { this.tipoCredito = tipoCredito; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public int getCuotas() { return cuotas;}
    public void setCuotas(int cuotas) {this.cuotas = cuotas;}
}