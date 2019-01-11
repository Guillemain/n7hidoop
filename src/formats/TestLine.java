package formats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import formats.Format.OpenMode;



public class TestLine {

	FormatLine lf;
	File fichier;
	
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	
	@Before
	public void setup (){


	    try {
	            fichier = new File("fichierTestFormatLine");
	            fichier.createNewFile();
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        line1 = "première ligne";
	        line2 = "deuxième ligne";
	        line3 = "troisième ligne";
	        line4 = "quatrième ligne";
	        System.out.println(1);
	        lf = new FormatLine(fichier.getName());
	    }

	    @Test
	    public void TesterEcritureLecture(){
	        // ouvrir en écriture
	    	System.out.println(2);
	        lf.open(OpenMode.W);
	        System.out.println(3);

	        // ecrire
	        lf.write(new KV("1", line1));
	        lf.write(new KV("2", line2));
	        lf.write(new KV("3", line3));
	        lf.write(new KV("4", line4));
	        System.out.println(4);
	        // fermer
	        lf.close();
	        System.out.println(5);
	        // ouvrir en lecture
	        lf.open(OpenMode.R);
	        System.out.println(6);
	        // lire
	        KV ligne1 = lf.read();
	        KV ligne2 = lf.read();
	        KV ligne3 = lf.read();
	        KV ligne4 = lf.read();
	        System.out.println(7);
	        // tester la lecture et l'écriture
	        assertEquals("ligne 1 valeur", line1, ligne1.v);
	        assertEquals("ligne 1 clé", "1", ligne1.k);
	        System.out.println("ici");
	        assertEquals("ligne 2 valeur", line2, ligne2.v);
	        assertEquals("ligne 2 clé", "2", ligne2.k);
	        System.out.println("la");
	        assertEquals("ligne 3 valeur", line3, ligne3.v);
	        assertEquals("ligne 3 clé", "3", ligne3.k);
	        System.out.println("here");
	        assertEquals("ligne 4 valeur", line4, ligne4.v);
	        assertEquals("ligne 4 clé", "4", ligne4.k);
	        System.out.println(8);
	        // fermer
	        lf.close();
	        System.out.println(9);
	    }

	    @Test
	    public void testerOuvertureLecture(){
	    	System.out.println("10");
	    	lf.open(OpenMode.R);

	        // tester l'ouverture
	        assertTrue("lecture", lf.oLect);
	        assertFalse("ecriture", lf.oEcriture);
	        assertNotNull("Buffer", lf.buffer);

	        lf.close();
	    }

	    @Test
	    public void testerOuvertureEcriture(){
	    	System.out.println("11");
	        lf.open(OpenMode.W);

	        // tester l'ouverture
	        assertFalse("lecture", lf.oLect);
	        assertTrue("ecriture", lf.oEcriture);
	        assertNotNull("FileWriter", lf.fichierEcriture);

	        lf.close();
	    }

	    @Test
	    public void testerFermeture(){
	    	System.out.println("12");
	        lf.open(OpenMode.R);
	        lf.close();

	        // tester la fermeture
	        assertFalse("lecture", lf.oLect);
	        assertFalse("ecriture", lf.oEcriture);    
	    }

	    @Test
	    public void testerGetIndex(){
	    	System.out.println("13");
	        lf.open(OpenMode.R);
	        assertEquals("index", 0, lf.getIndex());
	        System.out.println(lf.getIndex());

	        lf.read();
	        
	        
	        assertEquals("index", 1, lf.getIndex());
	        System.out.println(lf.getIndex());
	        lf.read();
	        System.out.println(lf.getIndex() + " jjjj");
	        assertEquals("index", 2, lf.getIndex());
	        System.out.println(lf.getIndex());
	        lf.read();
	        assertEquals("index", 3, lf.getIndex());
	        System.out.println(lf.getIndex());
	        lf.read();
	        assertEquals("index", 4, lf.getIndex());
	        System.out.println(lf.getIndex());
	        lf.close();
	        assertEquals("index", 0, lf.getIndex());
	        System.out.println(lf.getIndex());

	    }

	    @Test
	    public void testerGetFName(){
	    	System.out.println("14");
	        assertEquals("getFName", "fichierTestFormatLine", lf.getFname());
	    }

	    @Test
	    public void testerSetFName(){
	    	System.out.println("15");
	        lf.setFname("test");
	        assertEquals("getFName", "test", lf.getFname());
	        lf.open(OpenMode.R);
	        
	        assertEquals("premiere ligne", line1, lf.read());
	        lf.close();
	    }

	    @After
	    public void tearDown(){
	        fichier.delete();
	    }


}
