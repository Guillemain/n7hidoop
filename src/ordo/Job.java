package ordo;

import map.MapReduce;
import map.Mapper;
import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import com.sun.xml.internal.bind.v2.TODO;

/**
 * @author Guillemain Implémentation de JobInterface;
 * 
 * 
 */
public class Job extends Thread implements JobInterface {
	
	private Lock moniteur;
    // La liste des machines où se trouve un daemon.
    private HashMap<String, String> listeMachine;
    // La liste des objets RMI deamon, si jamais on souhaite leur faire passer un
    // message.
    private HashMap<String, Daemon> listeNode;
    // La liste des etats des deamons;
    private HashMap<String, Boolean> listeEtatDaemon;
    // Adresse du Job.
    private String monAdresse;
    //
    private Format.Type typeFichier;
    //
    private String nomFichier;
    
    Condition GpasFINI;
    
    int portDaemons = 5571;

    public int getPortDaemons() {
		return portDaemons;
	}

	public void setPortDaemons(int portDaemons) { // <- On devrait peut être interdire ça... mais c'est bien pratique quand même...
		try {
    		listeMachine = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader("../config/listeMachines.txt"));
		    String line;
		    while ((line = reader.readLine()) != null)
		    {
		    	listeMachine.put(line, ("//" + line + ":"+ portDaemons +"/Daemon"));
		    }
		  	reader.close();
		} catch (Exception e) {
			System.err.println("config/listeMachines.txt absent.");
		}
		this.portDaemons = portDaemons;
	}

	// Constructeur :
    public Job() {
    	try {
    		moniteur = new ReentrantLock();
    		GpasFINI = moniteur.newCondition();
    		listeMachine = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader("../config/listeMachines.txt"));
		    String line;
		    while ((line = reader.readLine()) != null)
		    {
		    	listeMachine.put(line, ("//" + line + ":"+ portDaemons +"/Daemon"));
		    }
		  	reader.close();
		} catch (Exception e) {
			System.err.println("config/listeMachines.txt absent.");
		}
    }

    // Méthodes requises pour la classe Job
    public void setInputFormat(Format.Type ft) {
        typeFichier = ft;
    }

    // Méthodes requises pour la classe Job
    public void setInputFname(String fname) {
        nomFichier = fname;
    }

    // Méthodes requises pour la classe Job
    public void startJob(MapReduce mr) {
    	
        listeNode = new HashMap<>();
        listeEtatDaemon = new HashMap<>();
        System.out.println(" <= Lancement du Map-Reduce =>");
        try {

            GestionnaireCB newGCBI = new GestionnaireCBImpl(this);
            System.out.print(" => Lancement du server de gestion des CallBacks à l'adresse : ");
            String urlCB = "//" + InetAddress.getLocalHost().getHostName() + ":" + "6969" + "/gcb";
            try {
            	Registry reg = LocateRegistry.createRegistry(6969);
                Naming.rebind(urlCB, newGCBI);
                System.out.print(urlCB);
                System.out.println(" => Server GCB lancé \n \n \n ");
            } catch (Exception e) {
                System.err.println("ERREUR dans la création de l'url du GCB");
                e.printStackTrace();
            }
            
            System.out.print(" <= Contact des Daemons :");
            /**
             * Pour chaque daemon on crée un Callback avec son identifiant. On met à jour
             * aussi la table des Daemon en cours de calculs.
             */
            for (Map.Entry<String, String> entry : listeMachine.entrySet()) {
                String url = entry.getValue();
                System.out.print(" -> Recherche du Daemon : " + url);
                Daemon node = (Daemon) Naming.lookup(url);
                System.out.println(" Daemon trouvé !");
                String id = entry.getKey();
                listeNode.put(id, node);
                listeEtatDaemon.put(id, false);
                Callback cb = (Callback) new CallbackImpl(urlCB, id);
                node.runMap((Mapper) mr,(Format) null, (Format) null, cb); // Format tout ça...
                //node.ping(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" Contact terminé =>");
        System.out.println(" <= Attente des résultats ...");
        boolean cond = false; // <- Condition de fin d'attente.
        while (!cond) {
        	moniteur.lock();
        	cond = true;
            for (Map.Entry<String, Boolean> entry : listeEtatDaemon.entrySet()) {
            	System.out.println(entry.getKey() + entry.getValue());
                cond = (cond && entry.getValue());
            }
            System.out.println(cond);
            if (!cond) {
	            try { // On gêle en attedant les notifys.
	                GpasFINI.await();
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                System.out.println("Problème dans le wait. On echape à la boucle while active, -> Fin du deamon");
	                e.printStackTrace();
	                System.exit(0);
	            }
            }
            moniteur.unlock();
        }
    	System.out.println(" fin des Calculs ! =>");
        // Reduce //
        // TODO   //
        // ------ //
        
        System.err.println("FINI");
        System.exit(0);
    }

    /**
     * Fonction qui permet de mettre à jour la table d'activités des daemons
     */
    void notifierFinCalcul(String id) {
    	moniteur.lock();
        listeEtatDaemon.put(id, true);
        GpasFINI.signal(); // On dé-gêle le runMap.
        moniteur.unlock();
    }
}
