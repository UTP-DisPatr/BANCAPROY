package Patrones;

import Dao.IAnalistaDAO;

import Modelo.Analista;

public class AnalistaFacade {
    

    private IAnalistaDAO dao = new AnalistaProxy();

    public Analista iniciarSesion(String usuario, String password) {

        return dao.login(usuario, password);
    }
}