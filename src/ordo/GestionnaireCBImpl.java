package ordo;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.omg.SendingContext.RunTime;

import map.Mapper;
import formats.Format;

public class GestionnaireCBImpl extends UnicastRemoteObject implements GestionnaireCB  {

    private Job monJobManager;
    
    public GestionnaireCBImpl(Job job, String url) throws RemoteException{
        GestionnaireCB newGCBI = new GestionnaireCBImpl(job);
        Naming.rebind(url, newGCBI);
    }
    public GestionnaireCBImpl(Job job) {
        monJobManager = job;
    }

    public void notifierFinCalcul(String id){
        monJobManager.notifierFinCalcul(id);
    }
    
}