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
    		Socket sock = new Socket ("verlaine",4000);
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            
            oos.writeObject(Commande.CMD_DELETE);
            oos.writeObject(hdfsFname);
            
            oos.close();
            ois.close();
            
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
    		Socket sock = new Socket("verlaine",4000);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
        	ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            
            FileReader fr = new FileReader(fichier);
            BufferedReader buff = new BufferedReader(fr);
            
            /** Pour fragmenter
            buff.mark(8192);
            int nbLigne = 0;
            while (buff.readLine() != null){
            	nbLigne ++;
            }
            buff.reset();
            
            String str = new String();
            for (int j=0; j<nbLigne; j++){
            	str+= buff.readLine()+"\n";
            }*/
            
            oos.writeObject(Commande.CMD_WRITE);
            oos.writeObject(fichier.getName());
            oos.writeObject(fmt);
            oos.writeObject(localFSSourceFname); //Remplacer par str après
            
            oos.close();
            ois.close();
            
    		
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
        	
            Socket sock = new Socket ("verlaine",4000);
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
       
            oos.writeObject(Commande.CMD_READ);
            oos.writeObject(hdfsFname);
        
            byte[] buffer = new byte[1024];
            int readbytes;
            while((readbytes = ois.read(buffer)) > 0){
            	fos.write(buffer, 0, readbytes);
            }

            oos.close();
            ois.close();


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
