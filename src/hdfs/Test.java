package hdfs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import formats.Format;
import formats.Format.Type;
import formats.KV;
import formats.FormatImpl;
import formats.Commande;


public class Test {
	

	public static void main(String[] args) {
		

		FormatImpl lf = new FormatImpl("/home/mpelissi/nosave/DATA_Projet_Hidoop/toto.txt",Type.LINE);

		lf.open(Format.OpenMode.R);
		KV kv = lf.read();
		System.out.println(kv);
		
		kv = lf.read();
		System.out.println(kv);
		
		kv = lf.read();
		System.out.println(kv);
		
		kv = new KV("Cours","Math");
		lf.open(Format.OpenMode.W);
		lf.write(kv);
		
		kv = new KV("Prof","Ellips");
		lf.write(kv);

		
		lf.close();
		

		FormatImpl lk = new FormatImpl("/home/mpelissi/nosave/DATA_Projet_Hidoop/totokv.txt",Type.KV);

		lk.open(Format.OpenMode.R);
	
		kv = lk.read();
		System.out.println(kv);
		
		kv = lk.read();
		System.out.println(kv);
		
		kv = lk.read();
		System.out.println(kv);
	
		kv = new KV("Cours","Math");
		lk.open(Format.OpenMode.W);
		lk.write(kv);
		
		kv = new KV("Prof","Ellips");
		lk.write(kv);

		lk.close();


	}

}
