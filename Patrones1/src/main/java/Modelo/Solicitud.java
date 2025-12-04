package Modelo;

import Patrones.*; 
import java.sql.Date;
import java.util.ArrayList; 
import java.util.List;      

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

    // 1. NUEVO: Lista de Observadores (Patrón Observer) ---
    private List<Observador> observadores = new ArrayList<>();

    public Solicitud() {
        this.estadoObj = new EstadoPendiente();
        
        this.agregarObservador(new SistemaNotificacion());
    }
    
    // 2. Métodos del Patrón Observer 
    public void agregarObservador(Observador o) {
        this.observadores.add(o);
    }

    private void notificarObservadores(String mensaje) {
        for (Observador o : observadores) {
            o.actualizar(mensaje);
        }
    }

    // Método Aprobar 
public void aprobar() { 
        // 1. Patrón State
        this.estadoObj.aprobar(this); 
        
        
        String mensaje = "La Solicitud #" + this.idSolicitud + " por S/ " + this.monto + " fue APROBADA exitosamente.";
        notificarObservadores(mensaje);
    }

public void rechazar() { 
        this.estadoObj.rechazar(this); 
        
        String mensaje = "La Solicitud #" + this.idSolicitud + " del cliente " + this.nombreCliente + " ha sido RECHAZADA.";
        notificarObservadores(mensaje);
    }
    

    public void setEstadoObj(EstadoSolicitud estado) {
        this.estadoObj = estado;
    }
    
    public String getEstadoString() {
        return estadoObj.getNombre();
    }
    
    public void setEstadoString(String estadoBD) {
        if (estadoBD.equalsIgnoreCase("APROBADO")) {
            this.estadoObj = new EstadoAprobado();
        } else if (estadoBD.equalsIgnoreCase("RECHAZADO")) {
            this.estadoObj = new EstadoRechazado();
        } else {
            this.estadoObj = new EstadoPendiente();
        }
    }

    // Getters y Setters 
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
    
    public String getEstado() { 
       
        return estadoObj.getNombre(); 
    }
    public void setEstado(String estado) { this.estado = estado; }
    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public int getCuotas() { return cuotas;}
    public void setCuotas(int cuotas) {this.cuotas = cuotas;}
}