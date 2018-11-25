package ordo;

import map.MapReduce;
import formats.Format;
import java.rmi.*;
/**
 * @author Guillemain
 * Implémentation de JobInterface;
*/
public class Job implements JobInterface{
    private List listeMachine;
    

    // Méthodes requises pour la classe Job  
	public void setInputFormat(Format.Type ft){
        System.err.println("ERREUR : setInputFormat non implémenté encore");
    }
    public void setInputFname(String fname){
        System.err.println("ERREUR : setInputFname non implémenté encore");
    }

    public void startJob (MapReduce mr){

    }

}