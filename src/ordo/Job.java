package ordo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import formats.Format;
import formats.FormatLine;
import formats.KV;
import hdfs.HdfsClient;
import hdfs.NameNodeInterface;
import map.MapReduce;
import map.Mapper;

//import com.sun.xml.internal.bind.v2.TODO;

/**
 * @author Guillemain Implémentation de JobInterface;
 * 
 * 
 */
public class Job extends Thread implements JobInterface {
	
	private Lock moniteur;
    // La liste des machines où se trouve un daemon.
    private List<String> listeMachine;
    // La liste des objets RMI deamon, si jamais on souhaite leur faire passer un
    // message.
    private	HashMap<String, String> listeURLDaemon;
    private HashMap<String, Daemon> listeNode;
    // La liste des etats des deamons;
    private HashMap<Integer, Boolean> listeEtatFragment;
    // Adresse du Job.
    private String monAdresse;
    //
    private Format.Type typeFichier;
    //
    private String nomFichier;
    
    private String path = "";

    private String prefixeRetour = "R";

    private String prefixeResultat = "Result";  
    
    Condition GpasFINI;
    
    int portDaemons = 5571;

    public int getPortDaemons() {
		return portDaemons;
	}

	public void setPortDaemons(int portDaemons) { // <- On devrait peut être interdire ça... mais c'est bien pratique quand même...
		try {
    		listeMachine = new ArrayList<>();
    		listeURLDaemon = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader("../config/listeMachines.txt"));
		    String line;
		    while ((line = reader.readLine()) != null)
		    {
		    	listeURLDaemon.put(line, ("//" + line + ":"+ portDaemons +"/Daemon"));
		    	listeMachine.add(line);
		    }
		  	reader.close();
		} catch (Exception e) {
			System.err.println("config/listeMachines.txt absent.");
			System.exit(-1);
		}
		this.portDaemons = portDaemons;
	}

	// Constructeur :
    public Job() {
    	try {
    		moniteur = new ReentrantLock();
    		GpasFINI = moniteur.newCondition();
    		listeMachine = new ArrayList<>();
    		listeURLDaemon = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader("../config/listeMachines.txt"));
		    String line;
		    while ((line = reader.readLine()) != null)
		    {
		    	listeURLDaemon.put(line, ("//" + line + ":"+ portDaemons +"/Daemon"));
		    	listeMachine.add(line);
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
        listeEtatFragment = new HashMap<>();

        
        System.out.println(" <= Lancement du Map-Reduce =>");
        try {
        	/*
        	 * Partie Map
        	 */

            GestionnaireCB newGCBI = new GestionnaireCBImpl(this);
            //System.out.print(" => Lancement du server de gestion des CallBacks à l'adresse : ");

            String urlCB = "//" + InetAddress.getLocalHost().getHostName() + ":" + "6969" + "/gcb"; //Le CallBack exotique.
            
            try {
            	Registry reg = LocateRegistry.createRegistry(6969);
                Naming.rebind(urlCB, newGCBI);
                //System.out.print(urlCB);
                //System.out.println(" => Server GCB lancé \n \n \n ");
            } catch (Exception e) {
                System.err.println("ERREUR dans la création de l'url du GCB");
                e.printStackTrace();
            }

            
            System.out.print(" <= Contact des Daemons :");
            /**
             * Pour chaque daemon on crée un Callback avec son identifiant. On met à jour
             * aussi la table des Daemon en cours de calculs. ainsi que les formats..
             */
            /** ANCIENNE METHODE.
            for (Map.Entry<String, String> entry : listeMachine.entrySet()) {
            	Format fmLect = new FormatLine(entry.getKey()+nomFichier);
            	Format fmEcrt = new FormatLine(entry.getKey()+prefixeRetour+nomFichier);
            	
                String url = entry.getValue();
                System.out.print(" -> Recherche du Daemon : " + url);
                Daemon node = (Daemon) Naming.lookup(url);
                System.out.println(" Daemon trouvé !");

                String id = entry.getKey();
                listeNode.put(id, node);
                listeEtatFragment.put(id, false);
                Callback cb = (Callback) new CallbackImpl(urlCB, id);
                node.runMap((Mapper) mr, fmLect , fmEcrt, cb);
            }*/
            
            int numfragment = 1;
			while (numfragment < HdfsClient.NB_Frag_A_Changer) {
				int numServeur = numfragment%listeMachine.size();
				//String nom_actuel = NameNodeInterface.path +listeMachine.get(numServeur)+ nomFichier + "_" + numfragment;
				Format fmLect = new FormatLine(NameNodeInterface.path + listeMachine.get(numServeur) + nomFichier + "_" + numfragment);
            	Format fmEcrt = new FormatLine(NameNodeInterface.path + listeMachine.get(numServeur) + nomFichier + prefixeRetour + "_" + numfragment );
            	
                String url = listeURLDaemon.get(listeMachine.get(numServeur));
                System.out.print(" -> Recherche du Daemon : " + url);
                Daemon node = (Daemon) Naming.lookup(url);
                System.out.println(" Daemon trouvé !");

               
                listeEtatFragment.put(numfragment, false);
                Callback cb = (Callback) new CallbackImpl(urlCB, numfragment);
                
                node.runMap((Mapper) mr, fmLect , fmEcrt, cb);
				
				numfragment++;
			}
            
            
            System.out.println(" Contact terminé =>");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         *  Partie Reduce : 
         */
        System.out.println(" <= Attente des résultats ...");
        boolean cond = false; // <- Condition de fin d'attente.
        
        while (!cond) {
        	moniteur.lock();
        	cond = true;
            for (Map.Entry<Integer, Boolean> entry : listeEtatFragment.entrySet()) {
            	//System.out.println(entry.getKey() + entry.getValue());
                cond = (cond && entry.getValue());
            }
            //System.out.println(cond);
            if (!cond) {
	            try { // On gêle en attedant les notifs.
	                GpasFINI.await();
	            } catch (Exception e) {
	                // ODO Auto-generated catch block
	                System.out.println("Problème dans le wait. On echape à la boucle while active, -> Fin du deamon");
	                e.printStackTrace();
	                System.exit(0);
	            }
            }
            moniteur.unlock();
        }
        
        
        System.out.print(" Fin des Calculs ! =>");
        
        // REDUCE //
        System.out.print(" Création du fichier des résultats =>");
        HdfsClient hc = new HdfsClient();
        hc.HdfsRead( nomFichier + prefixeRetour, "temp/" + nomFichier);
        System.out.println(" Fichier créé. ");

        try {
            System.out.print("path+prefixeRetour+nomFichier");
            Format fmReduce = new FormatLine(NameNodeInterface.path + "temp/" + nomFichier);
            fmReduce.open(Format.OpenMode.R);
            Format fmResultat = new FormatLine(NameNodeInterface.path + "Resultats/"+nomFichier+ prefixeResultat);
            KV m = new KV("hi", "PUTAIN DE TESTs");
            fmResultat.open(Format.OpenMode.W);
            fmResultat.write(m);
            System.out.println(" Reduce en cours ...");
            mr.reduce(fmReduce, fmResultat);
            System.err.println("FIN, fichier de résultat enregistré au nom de : "  + nomFichier + prefixeResultat );
            fmReduce.close();
            fmResultat.close();
            //hc.HdfsDelete( nomFichier + prefixeRetour);
            
        } catch (Exception e) {
           e.printStackTrace();
        }        
        
        //System.exit(0);
    }

    /**
     * Fonction qui permet de mettre à jour la table d'activités des daemons
     */
    void notifierFinCalcul(int id) {
    	moniteur.lock();
        listeEtatFragment.put(id, true);
        GpasFINI.signal(); // On dé-gêle le runMap.
        moniteur.unlock();
    }
}
