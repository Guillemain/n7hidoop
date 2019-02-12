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
                KV premiereLigne =  reader.read();
                System.out.println(premiereLigne.v);
                writer.write(premiereLigne.v);
			}
			
			@Override
			public void map(FormatReader reader, FormatWriter writer) {
				while ((kv = reader.read()) != null) {
					System.out.println(reader.read().v);
				}
			}
		};
		job.startJob(map);
		
	}
}
