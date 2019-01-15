package formats;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import formats.Format.OpenMode;

public class TestKV {

    FormatKV kvf;
    File fichier;

    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String key1;
     private String key2;
    private String key3;
    private String key4;
    
    @Before
    public void setUp(){        
        try {
            fichier = new File("fichierTestKVFormat");
            fichier.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        value1 = "valeur 1";
        value2 = "valeur 2";
        value3 = "valeur 3";
        value4 = "valeur 4";
        
        key1 = "clé 1";
        key2 = "clé 2";
        key3 = "clé 3";
        key4 = "clé 4";
        
        kvf = new FormatKV(fichier.getName());
    }

    @Test
    public void TesterLectureEcriture(){
        // ouvrir en écriture
        kvf.open(OpenMode.W);

        // ecrire
        kvf.write(new KV(key1, value1));
        kvf.write(new KV(key2, value2));
        kvf.write(new KV(key3, value3));
        kvf.write(new KV(key4, value4));

        // fermer
        kvf.close();

        // ouvrir en lecture
        kvf.open(OpenMode.R);

        // lire
        KV ligne1 = kvf.read();
        KV ligne2 = kvf.read();
        KV ligne3 = kvf.read();
        KV ligne4 = kvf.read();

        // tester la lecture et l'écriture
        assertEquals("ligne 1 valeur", value1, ligne1.v);
        assertEquals("ligne 1 clé", key1, ligne1.k);

        assertEquals("ligne 2 valeur", value2, ligne2.v);
        assertEquals("ligne 2 clé", key2, ligne2.k);

        assertEquals("ligne 3 valeur", value3, ligne3.v);
        assertEquals("ligne 3 clé", key3, ligne3.k);

        assertEquals("ligne 4 valeur", value4, ligne4.v);
        assertEquals("ligne4 clé", key4, ligne4.k);

        // fermer
        kvf.close();
    }

    @Test
    public void testerOuvertureLecture(){
        kvf.open(OpenMode.R);

        // tester l'ouverture
        assertTrue("lecture", kvf.oLect);
        assertFalse("ecriture", kvf.oEcriture);
        assertNotNull("Buffer", kvf.buffer);

        kvf.close();
    }

    @Test
    public void testerOuvertureEcriture(){
        kvf.open(OpenMode.W);

        // tester l'ouverture
        assertFalse("lecture", kvf.oLect);
        assertTrue("ecriture", kvf.oEcriture);
        assertNotNull("FileWriter", kvf.fichierEcriture);

        kvf.close();
    }

    @Test
    public void testerFermeture(){
        kvf.open(OpenMode.R);
        kvf.close();

        // tester la fermeture
        assertFalse("lecture", kvf.oLect);
        assertFalse("ecriture", kvf.oEcriture);    
    }

    @Test
    public void testerGetIndex(){
        kvf.open(OpenMode.R);
        assertEquals("index", 0, kvf.getIndex());

        kvf.read();
        assertEquals("index", 1, kvf.getIndex());

        kvf.read();
        assertEquals("index", 2, kvf.getIndex());

        kvf.read();
        assertEquals("index", 3, kvf.getIndex());

        kvf.read();
        assertEquals("index", 4, kvf.getIndex());

        kvf.close();
        assertEquals("index", 0, kvf.getIndex());

    }

    @Test
    public void testerGetFName(){
        assertEquals("getFName", "fichierTestKVFormat", kvf.getFname());
    }

    @Test
    public void testerSetFName(){
        kvf.setFname("testfile");
        assertEquals("getFName", "testfile", kvf.getFname());
        kvf.open(OpenMode.R);
        assertEquals("premiere ligne", key1 + KV.SEPARATOR + value1, kvf.read());
        kvf.close();
    }

    @After
    public void tearDown(){
        fichier.delete();
    }
}