package hdfs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import formats.Commande;
import formats.Format;
import formats.Format.Type;
import formats.KV;
//import formats.KVFormat;
//import formats.LineFormat;

public class HdfsServer {

	public static void main(String[] args) throws IOException{
		try {
			int port = Integer.parseInt(args[0]);
		
			ServerSocket ss;
			ss = new ServerSocket(port);
			System.out.println("Serveur démarré :");
		
			while(true){
				// On ouvre un second socket
				Socket sock = ss.accept();
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            	ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
		
		
				Commande cmd = (Commande) ois.readObject();
				
				switch(cmd){
				case CMD_READ:
					System.out.println("Demande de lecture reçue par le serveur");
					String fnameR = (String) ois.readObject();
					
					FileReader fr = new FileReader(fnameR);
					BufferedReader buff = new BufferedReader(fr);
					// Message à envoyer
					String str = new String();
					String line;
					while((line = buff.readLine()) != null){
						str += line + "\n";
					}
					buff.close();
					
					oos.writeObject(str);
					System.out.println("Message envoyé");
					
					
					break;
				case CMD_WRITE:
					System.out.println("Demande d'écriture reçue par le serveur");
					String fnameW = (String) ois.readObject();
					Type fmtW = (Type) ois.readObject();
					
					FileWriter fw = new FileWriter(fnameW);
					
					// Reception du texte
					String strW = (String) ois.readObject();
					fw.write(strW,0,strW.length()-1);
					
					
				    // On ferme le fichier d'écriture
					fw.close();
					System.out.println("Fin demande écriture");
					
					
					break;
				case CMD_DELETE:
					System.out.println("Demande de suppresion reçue par le serveur");
					String fnameD = (String) ois.readObject();
					File f = new File(fnameD);
					f.delete();
					System.out.println("Fichier " + fnameD + "supprimé");
					break;
				default:
					break;
				}
				ois.close();
				oos.close();
				
			
			
			}
		} catch (ClassNotFoundException e) {
				System.out.println("Error dans HDFS Server");
				e.printStackTrace();
			}	
		
		
	}
}