package ordo;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.omg.SendingContext.RunTime;

public interface GestionnaireCB extends Remote{

    public void notifierFinCalcul(String id);
    
}