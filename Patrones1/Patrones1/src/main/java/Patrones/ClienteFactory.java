package Patrones;

import Modelo.Cliente;

public class ClienteFactory {
    public static Cliente crearCliente(int id, String nombre, String apellido,
        String email, String telefono, String password, String dni) {
        return new Cliente(id, nombre, apellido, email, telefono, password, dni);
    }
}
