package ordo;

import java.util.HashMap;

import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import map.MapReduce;

public class testJob {

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
				System.out.println("Reduce !");
			}
			
			@Override
			public void map(FormatReader reader, FormatWriter writer) {
				System.out.println("Map !");
			}
		};
		job.startJob(map);
	}
}
