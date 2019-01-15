/* Fichier de base */
package formats;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;


import formats.Format.OpenMode;
import formats.Format.Type;

public class FormatImpl implements Format{
	private FileReader fichierLecture;
	private BufferedReader buffer;
	private FileWriter fichierEcriture;
    private String nameF;
    private ArrayList<String> listeLigne = new ArrayList<>();
    private Type fmt;

	// Ici \|/ ****************************************************
    private int index = 0; // --> ATTENTION ON COMMENCE A ZERO !!! <--  COMMENTAIRE POUR MARIE
    //  Là /|\ ****************************************************
    
    public Type getFmt() {
		return fmt;
	}

	public void setFmt(Type fmt) {
		this.fmt = fmt;
	}
    
    // Gestion des threads : 
    boolean oLect = false; boolean oEcriture = false;

    public FormatImpl(String name, Type t){
    	fmt = t;
        nameF = name;
    }

	public void open(OpenMode mode){
        try {
            File fichier = new File(nameF);
            if (mode == OpenMode.R){
            	System.out.print("Ouverture du fichier " + nameF + " en mode lecture ");
            	oLect = true;
                // On ouvre le fichie en lecture
                fichier.setReadable(true);
                fichierLecture = new FileReader(fichier);
                buffer = new BufferedReader(fichierLecture);
                String ligne;
                // On lit le contenu du fichier.
                while ((ligne = buffer.readLine())!= null){ // On charge tout la rame /!\  ATTENTION FICHIER LOURD
                	listeLigne.add(ligne);
                }
                buffer.close();
                System.out.println("\nLecture des lignes faites");
            } else {
            	System.out.println("Ouverture du fichier "+ nameF + " en ecriture.");
            	oEcriture = true;
            	fichier.setWritable(true);
            	fichierEcriture = new FileWriter(fichier, true);            	
            }
        } catch (Exception e) {
            System.err.println("\nImpossible d'ouvrir le fichier.");
            e.printStackTrace();
        } 
    }
	
	public void close(){
		try {
			if(oEcriture){
				fichierEcriture.close();
				oEcriture = false;
			}
			if(oLect){
				fichierLecture.close();
				oLect = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long getIndex(){
		return index;
	}
	
	public String getFname(){
		return nameF;
	}
    public void setFname(String fname){
    	this.nameF = fname;
    }

	@Override
	public KV read() {
		if(!oLect){
			System.err.println("Opértation interdite");
			return null;
		}
		
		if(index >= listeLigne.size()){
			System.out.println("Il n'y a plus de ligne à lire.");
			return null;
		}
		KV retour = new KV();
		if (fmt == Type.LINE){
			retour =  new KV(Integer.toString(index + 1), listeLigne.get(index));

		} else {
			try {
				String[] lignes = listeLigne.get(index).split(KV.SEPARATOR);
				retour = (new KV(lignes[0],lignes[1]));
			} catch (Exception e) {
				System.out.println("ERREUR DE LECTURE. Ligne : " + Integer.toString(index) );
			}
		}
		index++;
		return retour;
	}

	@Override
	public void write(KV record) {
		if(!oEcriture){
			System.err.println("Opértation interdite");
			return;
		}
		
		if(fmt == Type.LINE){
			try {
				fichierEcriture.write(record.v + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				fichierEcriture.write(record.k + KV.SEPARATOR + record.v + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}