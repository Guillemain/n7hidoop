package ordo;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import formats.Format;

public interface Callback extends Serializable{
	public String getAdresseRetour();
	public String getID();
}
