package ordo;

import map.MapReduce;
import formats.Format;

/** Ajout par Guillemain : Spécification des méthodes ;) */
public interface JobInterface {
    // Méthodes requises pour la classe Job
    /** Initialise le format du fichier sur lequel nous allons travailler */
    public void setInputFormat(Format.Type ft);

    /** Initialise le nom du fichier sur lequel on souhaite travailler */
    public void setInputFname(String fname);

    /** éxécute le MapReduce sur les machines */
    public void startJob(MapReduce mr);
}