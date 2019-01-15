package ordon;

import java.util.HashMap;

import formats.FormatReader;
import formats.FormatWriter;
import map.MapReduce;

public class testJob {

	public static void main(String[] args) {
		HashMap<String, String> listenodes = new HashMap<>();
		System.out.println("NIKE");
		for (int i = 0; i < args.length-1; i = i+2){
			listenodes.put(args[i], args[i+1]);
		}
		System.out.println(listenodes);
		Job job = new Job();
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
