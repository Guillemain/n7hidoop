package ordo;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import formats.Format;
import map.Mapper;

/**
 * @author Guillemain : Guillemain, Implémentation de l'interface Daemon. Id Est
 *         : Le serveur qui trourne sur la machine dans l'espoir qu'un jour,
 *         lui donne une tâche à accomplir.
 * 
 *         $ > java Daemon URL
 * 
 */
/* A exécuter sur les machines serveur */
public class DaemonImpl extends UnicastRemoteObject implements Daemon, Runnable {
    
    private static String path = "../data/";
	Mapper mapper;
	Format reader;
	Format writer;
	Callback cb;
    
    String nomDuDaemon="DaemonSansNom";
    
    public DaemonImpl(Mapper m, Format reader, Format writer, Callback cb) throws RemoteException {
    	this.mapper = m;
    	this.reader = reader;
    	this.writer = writer;
    	this.cb = cb;
    }
    
	public String getNomDuDaemon() {
		return nomDuDaemon;
	}

	public void setNomDuDaemon(String nomDuDaemon) {
		this.nomDuDaemon = nomDuDaemon;
	}
	
	public static void main(String[] args) {
        try {
            // Creation du serveur des noms : 
        	Registry reg = LocateRegistry.createRegistry(Integer.valueOf(args[0]));
            // On crée l'objet serveur
            Daemon srvDaemon = new DaemonImpl();
            // On nomme le daemon (utile pour le debuguage)
            ((DaemonImpl) srvDaemon).setNomDuDaemon(InetAddress.getLocalHost().getHostName());
            // On crée l'url
            String url = "//" + InetAddress.getLocalHost().getHostName()+":"+ args[0]+"/Daemon";
            // On s'enregistre au près du serveur des noms :
            Naming.rebind(url, srvDaemon);
            System.out.println("<= Daemon lancé à l'url : " + url +" =>");
        } catch (Exception exe) {
            System.out.println("ERREUR, fin du daemon.");
            exe.printStackTrace();
        }
    }

    protected DaemonImpl() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    } 

    public void runMap(Mapper m, Format reader, Format writer, Callback cb) {
    	try {
			Thread t =new Thread(new DaemonImpl(m,reader,writer,cb));
			t.start();
		} catch (Exception e) {
			System.out.println("Erreur dans le thread");
		}
    }

	@Override
	public void ping(Callback cb) throws RemoteException {
		try {
            //System.out.print(" => " + nomDuDaemon + " On annonce au mainNode que nous avons été pingué ! : ");
            GestionnaireCB gcb = (GestionnaireCB) Naming.lookup(cb.getAdresseRetour());
            gcb.notifierFinCalcul(cb.getID());
            //System.out.println(" => annonce passée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void run() {
		try {
			try {
				reader.setFname(reader.getFname());
				reader.open(Format.OpenMode.R);
			} catch (Exception e) {
				System.out.println("Erreur dans " +  reader.getFname());
			}
            
			try {
				writer.setFname(writer.getFname());
				writer.open(Format.OpenMode.W);
			} catch (Exception e) {
				System.out.println("Erreur dans " +  writer.getFname());
			}
            
	        //System.out.print("<=" + nomDuDaemon + "Requête reçue :"); // tout est dit ?
	        mapper.map(reader, writer);
	        //System.out.print(" - Traitement fini - ");
		} catch (Exception e) {
			System.err.println("Erreur dans l'ouverture du fichier !");
			e.printStackTrace();
		} finally {
			try {
				 try {
			            //System.out.print(" => On annonce au mainNode que nous avons terminé : ");
			            GestionnaireCB gcb = (GestionnaireCB) Naming.lookup(cb.getAdresseRetour()); //On recupère le gcb 
			            gcb.notifierFinCalcul(cb.getID()); // On notifie le gcb qui va à son tour notifier Job.
			            //System.out.println(" => annonce passée.");
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
				reader.close();
				writer.close();
			} catch (Exception e) {
				System.err.println("Erreur dans la fermeture du fichier !");
			}
		}
    	
       
		
	}

}