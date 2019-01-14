package ordon;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import formats.Format;
import map.Mapper;

/**
 * @author Guillemain : Guillemain, Implémentation de l'interface Daemon. Id Est
 *         : Le serveur qui trourne sur la machine dans l'espoire qu'un jour,
 *         lui donne une tâche à accomplir.
 * 
 *         $ > java Daemon.class URL
 * 
 */
/* A exécuter sur les machines serveur */
public class DaemonImpl extends UnicastRemoteObject implements Daemon {


    protected DaemonImpl() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }

    public void runMap(Mapper m, Format reader, Format writer, Callback cb) {
        System.out.println("<= Requête reçue :"); // tout est dit ?
        m.map(reader, writer);
        System.out.println(" - Traitement fini - ");
        try {
            System.out.print(" => On annonce au mainNode que nous avons terminé : ");
            GestionnaireCB gcb = (GestionnaireCB) Naming.lookup(cb.getAdresseRetour());
            gcb.notifierFinCalcul(cb.getID());
            System.out.println(" => annonce passée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void ping(Callback cb) throws RemoteException {
		try {
            System.out.print(" => On annonce au mainNode que nous avons terminé : ");
            GestionnaireCB gcb = (GestionnaireCB) Naming.lookup(cb.getAdresseRetour());
            gcb.notifierFinCalcul(cb.getID());
            System.out.println(" => annonce passée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}