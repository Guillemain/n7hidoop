package ordo;
import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import formats.Format;

public interface Callback extends Remote{
	public void annoncerFinDeMap();
}
