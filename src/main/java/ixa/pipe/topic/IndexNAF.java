package ixa.pipe.topic;

import it.jrc.lt.evidx.Utils;
import it.jrc.lt.evidx.CalculateClassifiers;
import it.jrc.lt.evidx.Dictionary;
import java.io.*;
import java.util.Properties;

public class IndexNAF {
    private static Properties properties = new Properties();

    public IndexNAF(final Properties p){
	properties = p;
    }

    /* Based on assign function from EuroVocExecuter class */
    public File assign (File input) throws Exception {
	System.err.println("start indexing document");
	File outFile = File.createTempFile("assignTmp", ".xml", new File("/tmp"));
	properties.setProperty("output",outFile.getAbsolutePath());
	CalculateClassifiers cal = new CalculateClassifiers(properties);
	System.err.println("Building Classifier done.");
	Dictionary dict = new Dictionary(properties.getProperty(Utils.DICTIONARY));
	dict.load();
	File documentSet = File.createTempFile("EuroVocDocSet", "tmp");
	documentSet.deleteOnExit();
	Utils.createDocumentSet(input, dict, documentSet);
	int processed = cal.doAssigning(documentSet);
	System.err.println("Classification done.");
	return outFile;
    }
}
