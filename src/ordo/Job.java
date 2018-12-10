package ordo;

import map.MapReduce;
import formats.Format;
import java.rmi.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Guillemain Implémentation de JobInterface;
 */
public class Job extends Thread implements JobInterface {
    private HashMap<String, String> listeMachine;
    private HashMap<String, Daemon> listeNode;

    // Méthodes requises pour la classe Job
    public void setInputFormat(Format.Type ft) {
        System.err.println("ERREUR : setInputFormat non implémenté encore");
    }

    public void setInputFname(String fname) {
        System.err.println("ERREUR : setInputFname non implémenté encore");
    }

    public void startJob(MapReduce mr) {
    	try {
    		for( Map.Entry<String, String> entry : listeMachine.entrySet() ){
        		String url = entry.getValue();
        		Daemon node = new DaemonImpl();
        		Naming.rebind(url, node);
        		listeNode.put(entry.getKey(), node);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
    	// Reduce //s
    		
    }
}
