/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import formats.Format;
import formats.KV;
//import formats.KVFormat;
//import formats.LineFormat;
import formats.Commande;

import java.io.*;
import java.net.*;

public class HdfsClient {

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {
    	System.out.println("Demande de suppression");
    	
    	try {
    		Socket sock1 = new Socket ("verlaine",4000);
            ObjectOutputStream oos1 = new ObjectOutputStream(sock1.getOutputStream());
            ObjectInputStream ois1 = new ObjectInputStream(sock1.getInputStream());
            
            Socket sock2 = new Socket ("rocket",2207);
            ObjectOutputStream oos2 = new ObjectOutputStream(sock2.getOutputStream());
            ObjectInputStream ois2 = new ObjectInputStream(sock2.getInputStream());
            
            
            oos1.writeObject(Commande.CMD_DELETE);
            oos1.writeObject(hdfsFname);
            
            oos2.writeObject(Commande.CMD_DELETE);
            oos2.writeObject(hdfsFname);
            
            oos2.close();
            ois2.close();
            
            oos1.close();
            ois1.close();
            
    	} catch (Exception e) {
    		System.out.println("Erreur HdfdDelate (Client)");
    		e.printStackTrace();
    	}
    }
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, 
     int repFactor) { 
    	System.out.println("Demande d'écriture");
    	
    	try {
    		File fichier = new File(localFSSourceFname);
    		Socket sock1 = new Socket ("verlaine",4000);
            ObjectOutputStream oos1 = new ObjectOutputStream(sock1.getOutputStream());
            ObjectInputStream ois1 = new ObjectInputStream(sock1.getInputStream());
            
            Socket sock2 = new Socket ("rocket",2207);
            ObjectOutputStream oos2 = new ObjectOutputStream(sock2.getOutputStream());
            ObjectInputStream ois2 = new ObjectInputStream(sock2.getInputStream());
            
            FileReader fr = new FileReader(fichier);
            BufferedReader buff = new BufferedReader(fr);
            
            // Compte du nombre de ligne du fichier
            buff.mark(8192);
            int nbLigne = 0;
            while (buff.readLine() != null){
            	nbLigne ++;
            }
            buff.reset();
            
            int quotient = nbLigne/2; //2 = Nb de serveurs
            int reste = nbLigne%2;
            
            //On envoie les donnes
            String str1 = new String();
            for (int j=0; j<quotient; j++){
            	str1+= buff.readLine()+"\n";
            }
            
            oos1.writeObject(Commande.CMD_WRITE);
            oos1.writeObject(fichier.getName());
            oos1.writeObject(fmt);
            oos1.writeObject(str1);
            
            String str2 = new String();
            for (int j=0; j<quotient+reste; j++){
            	str2+= buff.readLine()+"\n";
            }
            
            oos2.writeObject(Commande.CMD_WRITE);
            oos2.writeObject(fichier.getName());
            oos2.writeObject(fmt);
            oos2.writeObject(str2);
            
            oos2.close();
            ois2.close();
            
            oos1.close();
            ois1.close();
    		
    	} catch (Exception e) {
    		System.out.println("Erreur HdfdWrite (Client)");
    		e.printStackTrace();
    	}
    	
    }

    public static void HdfsRead(String hdfsFname, String localFSDestFname) {
        System.out.println("Demande de lecture");
        File fichier = new File(localFSDestFname);
        try {
        	FileOutputStream fos = new FileOutputStream(fichier);
        	
        	Socket sock1 = new Socket ("verlaine",4000);
            ObjectOutputStream oos1 = new ObjectOutputStream(sock1.getOutputStream());
            ObjectInputStream ois1 = new ObjectInputStream(sock1.getInputStream());
            
            Socket sock2 = new Socket ("rocket",2207);
            ObjectOutputStream oos2 = new ObjectOutputStream(sock2.getOutputStream());
            ObjectInputStream ois2 = new ObjectInputStream(sock2.getInputStream());
       
            oos1.writeObject(Commande.CMD_READ);
            oos1.writeObject(hdfsFname);
        
            byte[] buffer = new byte[1024];
            int readbytes;
            while((readbytes = ois1.read(buffer)) > 0){
            	fos.write(buffer, 0, readbytes);
            }
            
            oos2.writeObject(Commande.CMD_READ);
            oos2.writeObject(hdfsFname);
        
            byte[] buffer2 = new byte[1024];
            int readbytes2;
            while((readbytes2 = ois2.read(buffer2)) > 0){
            	fos.write(buffer2, 0, readbytes2);
            }

            oos2.close();
            ois2.close();
            
            oos1.close();
            ois1.close();


        } catch (Exception e) {
            System.out.println("Erreur HdfsRead (Client)");
            e.printStackTrace();
        }

     }

	
    public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        try {
            if (args.length<2) {usage(); return;}

            switch (args[0]) {
              case "read": HdfsRead(args[1],null); break;
              case "delete": HdfsDelete(args[1]); break;
              case "write": 
                Format.Type fmt;
                if (args.length<3) {usage(); return;}
                if (args[1].equals("line")) fmt = Format.Type.LINE;
                else if(args[1].equals("kv")) fmt = Format.Type.KV;
                else {usage(); return;}
                HdfsWrite(fmt,args[2],1);
            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
