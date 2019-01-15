package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * MainNode à contacter pour obtenir, liste des machines, gérer les chuncks Et cetera  
 */
public interface NameNode extends Remote {

    public void deployerFragments(String FName);

    public HashMap<String,String> dosmh(); // Donne la HashMap<Nom_server,Url> de celui-ci

}