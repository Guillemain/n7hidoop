/* une PROPOSITION de squelette, incomplète et adaptable... */


package hdfs;
import formats.Format;
import formats.KV;
import formats.FormatKV;
import formats.FormatLine;
import formats.Commande;

import java.io.*;
import java.net.*;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

public class HdfsClient {

	// Tableau des sockets pour les differents serveurs
	//private static Socket[] sock ;
	// Tableau des ObjectOutpuStream pour les differents serveurs
	//private static ObjectOutputStream[] oos;
	// Tableau des ObjectInpuStream pour les differents serveurs
	//private static ObjectInputStream[] ois;
	// Liste des noms de machines sur lesquels les serveurs ont été déployé
	private static List<String> listeMachine = new ArrayList<>();

	public static final int CHUNK_SIZE = 5;

	private int NB_Frag_A_Changer = 12;
	
	/** Methode permettant de connaitre le nombre de fragment d'un fichier */
	/* Paliant l'abscence d'implementatio NameNode */
	public int nombreDeFragment(String name){
		Format fm = new FormatKV("../config/tableFragment.kv");
		fm.open(Format.OpenMode.R);
		int nb = 0;
		KV kv;
		System.out.println(fm);
		while ((kv = fm.read()) != null) {
			if (name.equals(kv.k)){
				nb = Integer.parseInt(kv.v);
			}
		}
		fm.close();
		return nb;
	}

	

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
			// sock = new Socket[listeMachine.size()];
			// oos = new ObjectOutputStream[listeMachine.size()];
			// ois = new ObjectInputStream[listeMachine.size()];
		} catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public void HdfsDelete(String hdfsFname) {
    	System.out.println("Demande de suppression");
    	//String hdfsFname = path + nom;
    	NB_Frag_A_Changer = nombreDeFragment(hdfsFname);
    	try {
    		
			int numfragment = 0;
			while (numfragment < NB_Frag_A_Changer) {
				int numServeur = numfragment%listeMachine.size();

				Socket sock = new Socket(listeMachine.get(numServeur), (6000 + numServeur));
        		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
        		
				oos.writeObject(Commande.CMD_DELETE);
				oos.writeObject(NameNodeInterface.path +listeMachine.get(numServeur)+ hdfsFname + "_" + numfragment);
				oos.flush(); // la bonne habitude

				oos.close();
				sock.close();
				numfragment ++ ;
			}

    	} catch (Exception e) {
    		System.out.println("Erreur HdfdDelate (Client)");
    		e.printStackTrace();
    	}
    }
	
    public void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) { 
		System.out.println("Demande d'écriture");

		ArrayList<KV> fragment = new ArrayList<KV>();
		KV kv;
		Format fm;

		int fragmentTaille = 0;
		int numfragment = 0;
    	
    	try {
			if (fmt == Format.Type.LINE) { 
				fm = FormatLine.build(NameNodeInterface.path + localFSSourceFname);
			} else {
				fm = FormatKV.build(NameNodeInterface.path + localFSSourceFname);
			}
			fm.open(Format.OpenMode.R);

			while ((kv = fm.read()) != null) {
				
				fragmentTaille ++;//
				fragment.add(kv);
				
				if (fragmentTaille == CHUNK_SIZE) {
					
					writeFragment(fmt, numfragment%listeMachine.size() ,localFSSourceFname, fragment, numfragment);
					fragment = new ArrayList<KV>();
					fragmentTaille = 0;
					numfragment ++;
				}
			}

			if (fragment.size()!=0){
				writeFragment(fmt, numfragment%listeMachine.size() ,localFSSourceFname, fragment, numfragment);
			}
            
			fm.close();
			
			Format configFragment = new FormatKV("../config/tableFragment.kv");
            configFragment.open(Format.OpenMode.W);
            configFragment.write(new KV(localFSSourceFname,Integer.toString(numfragment)));
            configFragment.close();
            
            
    		
    	} catch (Exception e) {
    		System.out.println("Erreur HdfdWrite (Client)");
    		e.printStackTrace();
    	}
    	
    	
    }

    public void HdfsRead(String hdfsFname, String localFSDestFname) {

		//String localFSDestFname = path +  destFname;
		//String hdfsFname = path + nom;
    	NB_Frag_A_Changer = nombreDeFragment(hdfsFname);
        System.out.println("Demande de lecture");
        File fichier = new File(NameNodeInterface.path + localFSDestFname);
        try {

			int numfragment = 1; //Fameux bug sur le numéro 0
			FileWriter fw = new FileWriter(fichier);
			while (numfragment < NB_Frag_A_Changer) {
				int numServeur = numfragment%listeMachine.size();

				Socket sock =  new Socket(listeMachine.get(numServeur), (6000 + numServeur));
        		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());


				oos.writeObject(Commande.CMD_READ);
			 	oos.writeObject(NameNodeInterface.path + listeMachine.get(numServeur) + hdfsFname + "_" + numfragment );

				
				// Lecture du fichier reçu
				String receive = (String) ois.readObject();
				// Ecriture du résultat dans le fichier local
				fw.write(receive, 0, receive.length());

				oos.close();
				ois.close();
        		sock.close();
				numfragment ++ ;
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
                break;
              case "nbFrag":
            	  System.out.println(hc.nombreDeFragment(args[1]));
            	  break;

            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



private void writeFragment(Format.Type fmt, int num, String name, ArrayList<KV> fragment, int numF) {
	try {
		//sock[num] = new Socket(listeMachine.get(num), (6000 + num));
        //oos[num] = new ObjectOutputStream(sock[num].getOutputStream());

		Socket sock = new Socket(listeMachine.get(num), (6000 + num));
        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		Format fm;
		if (fmt == Format.Type.LINE) { 
				fm = FormatLine.build(NameNodeInterface.path + listeMachine.get(num) + name + "_" + numF);
			} else {
				fm = FormatKV.build(NameNodeInterface.path + listeMachine.get(num) + name + "_" + numF);
			}

		oos.writeObject(Commande.CMD_WRITE);
		oos.flush();
		oos.writeObject(NameNodeInterface.path + listeMachine.get(num) + name);
		oos.flush();
		oos.writeObject(fm);
		oos.flush();
		
		for (KV kv : fragment) {
			//System.out.println(kv);
			oos.writeObject(kv);
			oos.flush();
		}
		
		oos.writeObject("fini");
		oos.flush();
        oos.close();
        sock.close();
	} catch (Exception e) {
		System.out.println("ERREUR WRITEFRAGMENT");
		e.printStackTrace();
	}
}

}