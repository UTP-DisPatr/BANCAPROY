package Modelo;



public class Cliente {

    private int id_cliente;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String password;
    private double saldo;

    // ✅ Constructor vacío (OBLIGATORIO)
    public Cliente() {}

    // ✅ Constructor completo
    public Cliente(int id_cliente, String dni, String nombre, String apellido,
                   String email, String telefono, String password) {
        this.id_cliente = id_cliente;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
    }

    public int getId_Cliente() { return id_cliente; }
    public void setId_Cliente(int id_cliente) { this.id_cliente = id_cliente; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
