package ordo;

import map.MapReduce;
import map.Mapper;
import formats.Format;
import jdk.nashorn.internal.ir.ForNode;

import java.rmi.*;
import java.util.HashMap;
import java.util.Map;

import com.sun.xml.internal.bind.v2.TODO;

/**
 * @author Guillemain Implémentation de JobInterface;
 */
public class Job extends Thread implements JobInterface  {
    // La liste des machines où se trouve un daemon.
    private HashMap<String, String> listeMachine; 
    // La liste des objets RMI deamon, si jamais on souhaite leur faire passer un message.
    private HashMap<String, Daemon> listeNode;
    // La liste des etats des deamons;
    private HashMap<String,Boolean> listeEtatDaemon;
    // Adresse du Job.
    private String monAdresse;
    //
    private Format.Type typeFichier;
    //
    private String nomFichier;

    // Constructeur :
    public Job(){
        monAdresse = "@adresse";
       // Naming.rebind(monAdresse, this); blème...
    }

    public Job(String ad){
        monAdresse = ad;
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
    // /!\ A mettre en synchronized !!!
    public void startJob(MapReduce mr) {
        listeNode = new HashMap<>();
        listeEtatDaemon = new HashMap<>();
        System.out.println(" <= Lancement du Map-Reduce =>");
    	try {
            new GestionnaireCBImpl(this, monAdresse); // Le ramasse miette le killera un jour où l'autre...
            System.out.print(" <= Contacte des Daemons :");
            /**
             *  Pour chaque daemon on crée un Callback avec son identifiant.
             *  On met à jour aussi la table des Daemon en cours de calculs.
             */
    		for( Map.Entry<String, String> entry : listeMachine.entrySet() ){
        		String url = entry.getValue();
                Daemon node = (Daemon) Naming.lookup(url);
                String id = entry.getKey();
                listeNode.put(id, node);
                listeEtatDaemon.put(id, false);
                Callback cb = new CallbackImpl(monAdresse, id);
                //node.runMap((Mapper) mr, , writer, cb); // Format tout ça...
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println(" Contacte terminé =>");
        System.out.println(" <= Attente des résultats ...");
        
        boolean cond = false; // <- Condition de fin d'attente.
        while(!cond){
            for( Map.Entry<String, Boolean> entry : listeEtatDaemon.entrySet()){
                cond = (cond && entry.getValue());
            }
            wait(); // On gêle en attedant les notifys.
        }
    	
        // Reduce //
        //  TODO  //
        // ------ //
    		
    }

    /** Fonction qui permet de mettre à jour la table d'activités des daemons
    */
    synchronized void notifierFinCalcul(String id){
        listeEtatDaemon.put(id, true);
        this.notify(); // On dé-gêle le runMap.
    }
}
