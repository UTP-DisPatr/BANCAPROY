package Modelo;

public class SimuladorCredito {

    // 1. NUEVO MÉTODO: Define la tasa según el tipo
    public double obtenerTasaAnual(String tipoCredito) {
        if (tipoCredito == null) return 25.0; // Default por si acaso
        
        switch (tipoCredito.toUpperCase()) {
            case "HIPOTECARIO":
                return 9.5;  // Tasa baja (largo plazo)
            case "VEHICULAR":
                return 14.0; // Tasa media
            case "ESTUDIOS":
                return 12.0; // Tasa preferencial
            case "PERSONAL":
            default:
                return 22.5; // Tasa alta (más riesgo)
        }
    }

    // 2. TU MÉTODO DE CÁLCULO (Lo dejamos genérico para que reciba cualquier tasa)
    public double calcularCuota(double monto, int meses, double tasaAnual) {
        double tasaMensual = (tasaAnual / 100) / 12;
        double cuota = (monto * tasaMensual) / (1 - Math.pow(1 + tasaMensual, -meses));
        return Math.round(cuota * 100.0) / 100.0;
    }
}