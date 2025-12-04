package Modelo;

import java.security.MessageDigest;

public class Bloque {

    private int idBloque;
    private String datos;
    private String hashActual;
    private String hashAnterior;

    public Bloque(String datos, String hashAnterior) {
        this.datos = datos;
        this.hashAnterior = hashAnterior;
        this.hashActual = generarHash();
    }

    private String generarHash() {
    try {
        String input = datos + hashAnterior + System.currentTimeMillis();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    } catch (Exception e) {
        return null;
    }
}


    public String getDatos() {
        return datos;
    }

    public String getHashActual() {
        return hashActual;
    }

    public String getHashAnterior() {
        return hashAnterior;
    }
}
