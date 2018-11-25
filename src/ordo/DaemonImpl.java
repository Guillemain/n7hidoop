import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutionException;

import javafx.util.Callback;
import map.Mapper;

/**
 * @author Guillemain : Guillemain,
 * Implémentation de l'interface Daemon.
 *  Id Est : Le serveur qui trourne sur la machine dans l'espoire qu'un jour,
 *           lui donne une tâche à accomplir.
 *  
 *     $ > java Daemon.class URL
 * 
 */
/* A exécuter sur les machines serveur */
public class DaemonImpl extends UnicastRemoteObject implements Daemon {
    public static void main(String[] args) { 
        try{
            //On crée l'objet serveur 
            Daemon srvDaemon = new DaemonImpl();
            //On s'enregistre au près du serveur des noms :
            Naming.rebind(args[0], srvDaemon);
            System.out.println("<= Deamon lancé =>");
        } catch (Exception exe) {
            System.out.println("ERREUR, fin du daemon.");
            exe.printStackTrace();
        }
    }

    public void runMap (Mapper m, Format reader, Format writer, Callback cb){
        System.out.println("<= Requête reçue :"); // tout est dit ?
        m.map(reader, writer);
        

        // Mon travail est enfin achevé ici...
    }
}