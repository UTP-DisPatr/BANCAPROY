package Modelo;

public class Analista {
    private int idAnalista;
    private String nombre;
    private String usuario;
    private String password;
    private String rol;

    public Analista() {}

    public Analista(int idAnalista, String nombre, String usuario, String password, String rol) {
        this.idAnalista = idAnalista;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public int getIdAnalista() { return idAnalista; }
    public void setIdAnalista(int idAnalista) { this.idAnalista = idAnalista; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}