package ordon;

import map.MapReduce;
import map.Mapper;
import formats.Format;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import com.sun.xml.internal.bind.v2.TODO;

/**
 * @author Guillemain Implémentation de JobInterface;
 */
public class Job extends Thread implements JobInterface {
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

    // Constructeur :
    public Job() {

    }

    public Job(HashMap<String, String> li) {
        this.listeMachine = li;
    }

    public Job(String ad) {
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
    public void startJob(MapReduce mr) {
        listeNode = new HashMap<>();
        listeEtatDaemon = new HashMap<>();
        System.out.println(" <= Lancement du Map-Reduce =>");
        try {

            GestionnaireCB newGCBI = new GestionnaireCBImpl(this);
            System.out.print(" => Lancement du server de gestion des CallBacks à l'adresse : ");
            String urlCB = "//" + InetAddress.getLocalHost().getHostName() + ":" + 6969 + "/gcb";
            try {
            	Registry reg = LocateRegistry.createRegistry(6969);
                Naming.rebind(urlCB, newGCBI);
                System.out.print(urlCB);
                System.out.println(" => Server lancé");
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
                // node.runMap((Mapper) mr, , writer, cb); // Format tout ça...
                node.ping(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" Contact terminé =>");
        System.out.println(" <= Attente des résultats ...");

        boolean cond = false; // <- Condition de fin d'attente.
        while (!cond) {
        	cond = true;
            for (Map.Entry<String, Boolean> entry : listeEtatDaemon.entrySet()) {
                cond = (cond && entry.getValue());
            }
            try { // On gêle en attedant les notifys.
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("Problème dans le wait. On echape à la boucle while active, -> Fin du deamon");
                e.printStackTrace();
                System.exit(0);
            }
        }
        // ------ //
        // Reduce //
        // TODO   //
        // ------ //

    }

    /**
     * Fonction qui permet de mettre à jour la table d'activités des daemons
     */
    synchronized void notifierFinCalcul(String id) {
        listeEtatDaemon.put(id, true);
        this.notify(); // On dé-gêle le runMap.
    }
}
