
package Modelo;

public class SimuladorCredito {
    // Calcula la cuota mensual (Fórmula de amortización)
    public double calcularCuota(double monto, int meses, double tasaAnual) {
        double tasaMensual = (tasaAnual / 100) / 12;
        double cuota = (monto * tasaMensual) / (1 - Math.pow(1 + tasaMensual, -meses));
        return Math.round(cuota * 100.0) / 100.0; // Redondear 2 decimales
    }
}
