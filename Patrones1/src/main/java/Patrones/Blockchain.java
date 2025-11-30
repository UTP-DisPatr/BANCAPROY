package Patrones;

import Dao.BlockchainDAO;
import Modelo.Bloque;

public class Blockchain {

    private BlockchainDAO dao = new BlockchainDAO();

    public void registrarEvento(String datos) {
        String hashAnterior = dao.obtenerUltimoHash();
        Bloque nuevo = new Bloque(datos, hashAnterior);
        dao.insertarBloque(nuevo);
    }
}
