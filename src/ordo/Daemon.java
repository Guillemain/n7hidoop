package ordo;

/* Fichier de base */

import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import formats.Format;

public interface Daemon extends Remote {
	public void runMap(Mapper m, Format reader, Format writer, Callback cb) throws RemoteException;
	public void ping(Callback cb) throws RemoteException;
}
