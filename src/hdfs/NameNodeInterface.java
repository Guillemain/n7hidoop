package hdfs;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 	Interface du NameNode.
 * 	Sa fonction est de gérer la redondance au sein de hidoop. Et d'ainsi prévenir des pannes
 * 	éventuelles sur les machines.
 * 
 * @author aguillem3
 *
 */

public interface NameNodeInterface {
	
	/**
	 * @param nomDuFichier
	 * @return La hashMap avec comme clef le nom de la machine et en valeur la liste des fragments qu'il détient. Null si non diffusé.
	 * @author Florian
	 */
	public HashMap<String, ArrayList<String>> getCartographie(String nomDuFichier);
	
	/**
	 * Diffuse sur les machines les fragments du ficher avec l'assurance qu'un fragment sera traité au moins deux fois.
	 * @param nomFichier à publier
	 * publierFichier(nomFichier,2);
	 */
	public void publierFichier(String nomFichier);
	
	/**
	 * Diffuse sur les machines les fragments du ficher avec l'assurance qu'un fragment sera traité au moins @param redondance fois.
	 * @param nomFichier
	 * @param redondance Le nombre de fois que le fragment dupliqué
	 * @author En équipe 
	 */
	public void publierFichier(String nomFichier,int redondance);
	
	/**
	 * 
	 * @param nomDuFichier 
	 * @return Le nombre de fois qu'un fragment est dupliqué sur le réseau 
	 * @author Florian
	 */
	public int getRedondance(String nomDuFichier);
	
	/**
	 * Force l'actualisation de la cartographie.
	 * @author Guillemain
	 */
	public void checkCartographie();
	
	/**
	 * Permet de savoit si on doit attendre les autres daemon ou non.
	 * @param listeDesMachines dont on a encore le résultat.
	 * @return Si le nombre de machine ayant exprimé le résultat est suffisant pour un reduce.
	 * @author Florian
	 */
	public boolean estCeSuffisantMachine(ArrayList<String> listeDesMachines);
	
	/**
	 * Permet de savoit si on doit attendre les autres daemon ou non.
	 * @param listeDesFragments dont on a le résultat
	 * @return Si le nombre de fragment possédé est suffisant pour un reduce.
	 * @autor Florian
	 */
	public boolean estCeSuffisantFragments(ArrayList<String> listeDesFragments);
	
	
	

}
