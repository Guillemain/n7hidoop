package ordo;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.omg.SendingContext.RunTime;

 /* @author Guillemain. Cette interface est un server qui collecte les retours des démons.
 *         Et qui se charge de contacter le job ensuite pour le notifier. La
 *         notification de la fin d'un cacul se fait impérativement par l'envoie
 *         de l'id de deamon. Permettant alors au job d'être au courant des
 *         deamons encore en attentes.
 * 
 */

public interface GestionnaireCB extends Remote {

    public void notifierFinCalcul(int id) throws RemoteException;

}