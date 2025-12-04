package Patrones;

import Dao.AnalistaDAO;
import Dao.IAnalistaDAO;
import Modelo.Analista;

public class AnalistaProxy implements IAnalistaDAO {

    // El Proxy tiene una instancia del objeto REAL
    private AnalistaDAO daoReal;

    public AnalistaProxy() {
        this.daoReal = new AnalistaDAO();
    }

    @Override
    public Analista login(String usuario, String password) {
        Analista analista = null;
        if (usuario == null || password == null) {
            System.out.println("PROXY: Intento de login inválido (datos vacíos).");
            return null; 
        }
        System.out.println("PROXY: Verificando credenciales para: " + usuario);
        analista = daoReal.login(usuario, password);
        if (analista != null) {
            System.out.println("PROXY: Acceso CONCEDIDO a " + analista.getNombre());
        } else {
            System.out.println("PROXY: ALERTA DE SEGURIDAD - Intento fallido para usuario: " + usuario);
        }

        return analista;
    }
}