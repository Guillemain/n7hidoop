package ordo;

import map.MapReduce;
import map.Mapper;
import formats.Format;


import java.rmi.*;
import java.util.HashMap;
import java.util.Map;
import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;

public class Demov0Job implements MapReduce {

    public static void main(String args[]) {
        Job j = new Job();
        j.setInputFormat(Format.Type.LINE);
        j.setInputFname(args[0]);
        long t1 = System.currentTimeMillis();
        j.startJob(new Demov0Job());
        long t2 = System.currentTimeMillis();
        System.out.println("time in ms =" + (t2 - t1));
        System.exit(0);
    }

    @Override
    public void map(FormatReader reader, FormatWriter writer) {

    }

    @Override
    public void reduce(FormatReader reader, FormatWriter writer) {

    }

}
