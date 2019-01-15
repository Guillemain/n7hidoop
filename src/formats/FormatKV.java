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
	protected BufferedReader buffer;
	protected FileWriter fichierEcriture;
    private String nameF;
    private ArrayList<String> listeLigne = new ArrayList<>();

	// Ici \|/ ****************************************************
    private int index = 0; // --> ATTENTION ON COMMENCE A ZERO !!! <--  COMMENTAIRE POUR MARIE
    //  Là /|\ ****************************************************
    
    // Gestion des threads : 
    protected boolean oLect = false;
    protected boolean oEcriture = false;

    public FormatKV(String name){
        nameF = name;
    }

	@Override
	public KV read() {
		String line = null;
		if(oLect){
			try{
				line = buffer.readLine();
				this.index++;
				
			}catch (IOException e){
				e.printStackTrace();
			}
		} else {
			System.err.println("Message erreur de READ");
		}
		
		if(line != null){
			String[] kv = line.split(KV.SEPARATOR);
			return new KV(kv[0],kv[1]);
		} else {
			return null;
		}
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
            File fichier = new File(this.nameF);
            
            File parentDirs = fichier.getParentFile();
            if (parentDirs != null) {
                parentDirs.mkdirs();
            }
            
            if (mode == OpenMode.R){
            	System.out.print("Ouverture du fichier " + nameF + " en mode lecture -> ");
            	oLect = true;
                //On ouvre le fichie en lecture
                fichier.setReadable(true);
                fichierLecture = new FileReader(fichier);
                buffer = new BufferedReader(fichierLecture);

            } else {
            	System.out.println("Ouverture du fichier "+ nameF + " en mode ecriture.");
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
				//fichierLecture.close();
				oLect = false;
				buffer.close();
				index=0;
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
		if(!oLect && !oEcriture){
			this.nameF = fname;
		}else {
			System.err.println("Fermer le fichier avant de modifier son nom.");
		}

	}


}
