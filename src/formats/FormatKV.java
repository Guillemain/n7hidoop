package formats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import formats.Format.OpenMode;
import formats.Format.Type;

public class FormatKV implements Format {
	
	private static final long serialVersionUID = 1L;
	private FileReader fichierLecture;
	private BufferedReader buffer;
	private FileWriter fichierEcriture;
    private String nameF;
    private ArrayList<String> listeLigne = new ArrayList<>();

	// Ici \|/ ****************************************************
    private int index = 0; // --> ATTENTION ON COMMENCE A ZERO !!! <--  COMMENTAIRE POUR MARIE
    //  Là /|\ ****************************************************
    
    // Gestion des threads : 
    boolean oLect = false; boolean oEcriture = false;

    public FormatKV(String name){
        nameF = name;
    }

	@Override
	public KV read() {
		if (!oLect) {
			System.err.println("Opértation interdite");
			return null;
		}

		if (index <= listeLigne.size()) {
			return null;
		}

		KV retour = new KV();

		try {
			String[] lignes = listeLigne.get(index).split(KV.SEPARATOR);
			retour = (new KV(lignes[0], lignes[1]));
		} catch (Exception e) {
			System.out.println("ERREUR DE LECTURE. Ligne : " + Integer.toString(index));
		}

		index++;
		return retour;
	}

	@Override
	public void write(KV record) {
		if (!oEcriture) {
			System.err.println("Opértation interdite");
			return;
		}

		try {
			fichierEcriture.write(record.k + KV.SEPARATOR + record.v + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void open(OpenMode mode) {
		try {
            File fichier = new File(nameF);
            if (mode == OpenMode.R){
            	System.out.print("Ouverture du fichier " + nameF + " en mode lecture -> ");
            	oLect = true;
                //On ouvre le fichie en lecture
                fichier.setReadable(true);
                fichierLecture = new FileReader(fichier);
                buffer = new BufferedReader(fichierLecture);
                String ligne;
                // On lit le contenu du fichier.
                while ((ligne = buffer.readLine())!= null){ // On charge tout la rame /!\  ATTENTION FICHIER LOURD
                	listeLigne.add(ligne);
                }
                buffer.close();
                System.out.println("Lecture des lignes faites");
            } else {
            	System.out.println("Ouverture du fichier "+ nameF + "en ecriture.");
            	oEcriture = true;
            	fichier.setWritable(true);
            	fichierEcriture = new FileWriter(fichier, true);            	
            }
        } catch (Exception e) {
            System.err.println("Impossible d'ouvrir le fichier.");
            e.printStackTrace();
        }
	}

	@Override
	public void close() {
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

	@Override
	public long getIndex() {
		return index;
	}

	@Override
	public String getFname() {
		return nameF;
	}

	@Override
	public void setFname(String fname) {
		this.nameF = fname;

	}


}
