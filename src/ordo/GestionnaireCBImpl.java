package ordo;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.omg.SendingContext.RunTime;

import map.Mapper;
import formats.Format;

public class GestionnaireCBImpl extends UnicastRemoteObject implements GestionnaireCB {

    private Job monJobManager;
    private String url;

    public String getURL() {
        return url;
    }

    public GestionnaireCBImpl(Job job, String port) throws RemoteException, MalformedURLException {
        
    }

    public GestionnaireCBImpl(Job job) throws RemoteException, MalformedURLException {
        monJobManager = job;
    }

    public void notifierFinCalcul(int id) throws RemoteException {
    	System.out.println(id + " me signale la fin de son calcul.");
        monJobManager.notifierFinCalcul(id);
    }

}