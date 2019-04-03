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
		HashMap<String, String> listenodes = new HashMap<>();
		for (int i = 0; i < args.length-1; i = i+2){
			listenodes.put(args[i], args[i+1]);
		}
		System.out.println(listenodes);
		Job job = new Job();
		job.setPortDaemons(Integer.valueOf(args[0]));
		job.setInputFormat(Format.Type.LINE);
		job.setInputFname("spleen.txt");
		MapReduce map = new MapReduce() {
			
			@Override
			public void reduce(FormatReader reader, FormatWriter writer) {
				reader.read();
				reader.read();
				KV kv;
				while ((kv = reader.read()) != null) {
					writer.write(kv);
				}
			}
			
			@Override
			public void map(FormatReader reader, FormatWriter writer) {
				KV kv;
				while ((kv = reader.read()) != null) {
					//System.out.println(kv.v);
					writer.write(kv);
				}
			}
		};
		job.startJob(map);
		
	}
}
