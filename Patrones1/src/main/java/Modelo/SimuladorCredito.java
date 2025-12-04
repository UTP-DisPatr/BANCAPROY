package Modelo;

public class SimuladorCredito {

    // 1. MÉTODO: Define la tasa según el tipo
    public double obtenerTasaAnual(String tipoCredito) {
        if (tipoCredito == null) return 25.0; // Default por si acaso
        
        switch (tipoCredito.toUpperCase()) {
            case "HIPOTECARIO":
                return 9.5;  // Tasa baja 
            case "VEHICULAR":
                return 14.0; // Tasa media
            case "ESTUDIOS":
                return 12.0; // Tasa preferencial
            case "PERSONAL":
            default:
                return 22.5; // Tasa alta 
        }
    }

    // 2. MÉTODO DE CÁLCULO 
    public double calcularCuota(double monto, int meses, double tasaAnual) {
        double tasaMensual = (tasaAnual / 100) / 12;
        double cuota = (monto * tasaMensual) / (1 - Math.pow(1 + tasaMensual, -meses));
        return Math.round(cuota * 100.0) / 100.0;
    }
}