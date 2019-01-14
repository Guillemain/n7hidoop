package ordon;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.omg.SendingContext.RunTime;

import map.Mapper;
import formats.Format;

public class CallbackImpl implements Callback {
    private String adresseRetour; // Adresse pour recontacter le main
    private String monID; // Id du Callback, (avec lequel le daemon va savoir qu'il est)

    public CallbackImpl(String adrs, String id) {
        adresseRetour = adrs;
        monID = id;
    }

    public String getAdresseRetour() {
        return adresseRetour;
    }

    public String getID() {
        return monID;
    }

}