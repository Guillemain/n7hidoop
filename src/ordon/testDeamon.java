package ordon;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class testDeamon {
	public static void main(String[] args) {
        try {
            // Creation du serveur des noms : 
        	Registry reg = LocateRegistry.createRegistry(5000);
            // On crée l'objet serveur
            Daemon srvDaemon = new DaemonImpl();
            // On crée l'url
            String url = "//" + InetAddress.getLocalHost().getHostName()+":"+"5000"+"/Deamon_map_reduce";
            // On s'enregistre au près du serveur des noms :
            Naming.rebind(url, srvDaemon);
            System.out.println("<= Deamon lancé à l'url : " + url +" =>");
        } catch (Exception exe) {
            System.out.println("ERREUR, fin du daemon.");
            exe.printStackTrace();
        }
    }
}
