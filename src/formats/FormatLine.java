/* Fichier de base */
package formats;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;

import util.Message;
import formats.Format.OpenMode;
import formats.Format.Type;
import java.io.Serializable;

public interface FormatLine implements Format {

    private String nameF;

    public FormatLine(String name){
        nameF = name;
    }

	public void open(OpenMode mode){
        try {
            File fichier = new File(name);
            if (mode == OpenMode.R){
                //On ouvre le fichie en lecture
                fichier.setReadable(true);
                fichierLecture = new FileReader(file);
                buffer = new BufferReader(fichierLecture);
            }
        } catch (Exception e) {
            //TODO: handle exception
        } 
    }
	public void close();
	public long getIndex();
	public String getFname();
    public void setFname(String fname);
    


}
