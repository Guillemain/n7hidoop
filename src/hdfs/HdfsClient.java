/* une PROPOSITION de squelette, incomplète et adaptable... */


package hdfs;
import formats.Format;
import formats.KV;
import formats.FormatKV;
import formats.FormatLine;
import formats.Commande;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class HdfsClient {

	// Tableau des sockets pour les differents serveurs
	private static Socket[] sock ;
	// Tableau des ObjectOutpuStream pour les differents serveurs
	private static ObjectOutputStream [] oos;
	// Tableau des ObjectInpuStream pour les differents serveurs
	private static ObjectInputStream[] ois;
	// Liste des noms de machines sur lesquels les serveurs ont été déployé
	private static List<String> listeMachine = new ArrayList<>();

	private static String path = "../data";
	

	public HdfsClient() {
		try {
		// Récupérer le nom des machines serveurs
			BufferedReader reader = new BufferedReader(new FileReader("../config/listeMachines.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				listeMachine.add(line);
			}
			reader.close();

			// Initialiser les tableaux
			sock = new Socket[listeMachine.size()];
			oos = new ObjectOutputStream[listeMachine.size()];
			ois = new ObjectInputStream[listeMachine.size()];
		} catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public void HdfsDelete(String nom) {
    	System.out.println("Demande de suppression");
    	String hdfsFname = path + nom;
    	try {
    		
    		for (int i = 0; i< listeMachine.size();i++){
    			//Ouverture socket 
		  		sock[i] = new Socket(listeMachine.get(i), (5000+i));
		  		oos[i] = new ObjectOutputStream(sock[i].getOutputStream());
		  		ois[i] = new ObjectInputStream(sock[i].getInputStream());
		  		
		  		//Envoie de la commande et du nom du fichier
		  		oos[i].writeObject(Commande.CMD_DELETE);
	            oos[i].writeObject(listeMachine.get(i) + hdfsFname);
	            
	            oos[i].close();
	            ois[i].close();
		  	}

    	} catch (Exception e) {
    		System.out.println("Erreur HdfdDelate (Client)");
    		e.printStackTrace();
    	}
    }
	
    public void HdfsWrite(Format.Type fmt, String sourceFname, 
     int repFactor) { 
		System.out.println("Demande d'écriture");
		
		String localFSSourceFname = path +  sourceFname;
    	
    	try {
    		File fichier = new File(localFSSourceFname);
            FileReader fr = new FileReader(fichier);
            BufferedReader buff = new BufferedReader(fr);
            
            // Compte du nombre de ligne du fichier
            buff.mark(8192);
            int nbLigne = 0;
            while (buff.readLine() != null){
            	nbLigne ++;
            }
            buff.reset();
            
            int quotient = nbLigne/listeMachine.size(); 
            int reste = nbLigne%listeMachine.size();
            
			for (int i = 0; i < listeMachine.size(); i++) {
				// Ouverture socket
				sock[i] = new Socket(listeMachine.get(i), (5000 + i));
				oos[i] = new ObjectOutputStream(sock[i].getOutputStream());
				ois[i] = new ObjectInputStream(sock[i].getInputStream());
				
				
				// On envoie les donnes
				String str = new String();
				if (i == listeMachine.size()){
					for (int j = 0; j < quotient; j++) {
						str += buff.readLine() + "\n";
					}
				} else {
					for (int j = 0; j < quotient + reste; j++) {
						str += buff.readLine() + "\n";
					}
				}

				//Envoie de la commande et des donnes
				oos[i].writeObject(Commande.CMD_WRITE);
				oos[i].writeObject(listeMachine.get(i) + fichier.getName());
				oos[i].writeObject(fmt);
				oos[i].writeObject(str);
			}
            
    		
    	} catch (Exception e) {
    		System.out.println("Erreur HdfdWrite (Client)");
    		e.printStackTrace();
    	}
    	
    }

    public void HdfsRead(String nom, String destFname) {

		String localFSDestFname = path +  destFname;
		String hdfsFname = path + nom;

        System.out.println("Demande de lecture");
        File fichier = new File(localFSDestFname);
        try {
			FileWriter fw = new FileWriter(fichier);

			for (int i = 0; i < listeMachine.size(); i++) {
				// Ouverture socket
				sock[i] = new Socket(listeMachine.get(i), (5000 + i));
				oos[i] = new ObjectOutputStream(sock[i].getOutputStream());
				ois[i] = new ObjectInputStream(sock[i].getInputStream());

				oos[i].writeObject(Commande.CMD_READ);
				oos[i].writeObject(listeMachine.get(i) + hdfsFname );

				try {
					// Lecture du fichier reçu
					String receive = (String) ois[i].readObject();
					// Ecriture du résultat dans le fichier local
					fw.write(receive, 0, receive.length());

				} catch (UnknownHostException e) {
					System.out.println(" Un des hosts n'est pas reconnu lors de la reception  ");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.println(" Objet non reconnu pendant la reception");
					e.printStackTrace();
				}
				
				oos[i].close();
				ois[i].close();

			}
			
			fw.close();
			
            System.out.println("HdfsRead données dans le fichier local ");
        } catch (Exception e) {
            System.out.println("HdfsRead (Client)");
            e.printStackTrace();
        }

     }

	
    public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        try {

			// TO DO : faire un constructeur décent. 
			// Gerer les noms. 
			//  
        	
			// // Récupérer le nom des machines serveurs
			// BufferedReader reader = new BufferedReader(new FileReader("../config/listeMachines.txt"));
			// String line;
			// while ((line = reader.readLine()) != null) {
			// 	listeMachine.add(line);
			// }
			// reader.close();

			// // Initialiser les tableaux
			// sock = new Socket[listeMachine.size()];
			// oos = new ObjectOutputStream[listeMachine.size()];
			// ois = new ObjectInputStream[listeMachine.size()];


			HdfsClient hc = new HdfsClient();

			//Main
			if (args.length < 2) {
				usage();
				return;
			}

            switch (args[0]) {
              case "read": hc.HdfsRead(args[1],args[1]+".resultat"); break; //Fichier local : meme nom et extensions .resultat
              case "delete": hc.HdfsDelete(args[1]); break;
              case "write": 
                Format.Type fmt;
                if (args.length<3) {usage(); return;}
                if (args[1].equals("line")) fmt = Format.Type.LINE;
                else if(args[1].equals("kv")) fmt = Format.Type.KV;
                else {usage(); return;}
                hc.HdfsWrite(fmt,args[2],1);
            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
