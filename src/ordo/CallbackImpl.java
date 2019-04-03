package ordo;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.omg.SendingContext.RunTime;

import map.Mapper;
import formats.Format;

public class CallbackImpl implements Callback {
    private String adresseRetour; // Adresse pour recontacter le main
    private int monID; // Id du Callback, (avec lequel le daemon va savoir sur quel fragment il bosse)

    public CallbackImpl(String adrs, int id) {
        adresseRetour = adrs;
        monID = id;
    }

    public String getAdresseRetour() {
        return adresseRetour;
    }

    public int getID() {
        return monID;
    }
}