package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * MainNode à contacter pour obtenir, liste des machines, gérer les chunck Et cetera  
 */
public interface NameNode extends Remote {

    public void deployerFragment(String FName);

}