package ordo;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.omg.SendingContext.RunTime;


/**
 * @author Guillemain.
 * 
 *  Ce serveur à pour but de tourné à côté du deamon et de recevoir tout les rappelle.
 *  Son adresse url est passé dans un objet Callback sérialisable pour permettre au deamon de l'appeler. 
 *  
 * 
 */
public interface GestionnaireCB extends Remote{

    public void notifierFinCalcul(String id);
    
}