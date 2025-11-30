
package Dao;

import Modelo.Analista;

/**
 *
 * @author JHEINS
 */
public interface IAnalistaDAO {
    Analista login(String usuario, String password);
}