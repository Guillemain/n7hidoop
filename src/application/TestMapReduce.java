package application;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import map.MapReduce;
import ordo.Job;
import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;
import java.util.HashMap;


public class TestMapReduce {
	/**
	 * Simple tests de bases. Le fichier doit être rendu intégralement
	 * @param args port du daemon.
	 */
	public static void main(String[] args) {
		/*
		HashMap<String, String> listenodes = new HashMap<>();
		for (int i = 0; i < args.length-1; i = i+2){
			listenodes.put(args[i], args[i+1]);
		}
		System.out.println(listenodes);*/
	
		Job job = new Job();
		job.setPortDaemons(5510);//Integer.valueOf(args[0]));
		job.setInputFormat(Format.Type.LINE);
		job.setInputFname("spleen.txt");
		MapReduce map = new MapReduce() {
			
			@Override
			public void reduce(FormatReader reader, FormatWriter writer) {
				KV kv;
				int i = 0;
				while ((kv = reader.read()) != null) {
					//writer.write(kv);
					i += Integer.parseInt(kv.v);
				}
				KV resultat = new KV("R", Integer.toString(i));
				writer.write(resultat);
			}
			
			@Override
			public void map(FormatReader reader, FormatWriter writer) {
				int i = 0;
				KV kv;
				while ((kv = reader.read()) != null) {
					//System.out.println(kv.v);
					i++;
				}
				KV resultat = new KV("R", Integer.toString(i));
				writer.write(resultat);
			}
		};
		long debut = System.currentTimeMillis();
		System.out.println("=============POULOULOU=============");
		job.startJob(map);
		System.out.println("=============POULOULOU=============");
		System.out.println( System.currentTimeMillis()-debut);
		System.exit(0);
	}
	
}
