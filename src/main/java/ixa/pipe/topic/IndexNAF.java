package ixa.pipe.topic;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import it.jrc.lt.evidx.CalculateClassifiers;
import it.jrc.lt.evidx.Dictionary;
import it.jrc.lt.evidx.Utils;

public class IndexNAF {

	private Dictionary dict;
	private CalculateClassifiers cal;
	private File outFile;

	public IndexNAF(Properties properties){
		this.dict = new Dictionary(properties.getProperty(Utils.DICTIONARY));
		this.dict.load();
		try {
			this.outFile = File.createTempFile("assignTmp", ".xml", new File("/tmp"));
			properties.setProperty("output", this.outFile.getAbsolutePath());
			this.cal = new CalculateClassifiers(properties);
		} catch (IOException e) {
			LOGGER.error("Error init CalculateClassifiers", e);
		}
		
		LOGGER.info("Building Classifier done");
	}

	private static final Logger LOGGER = Logger.getLogger(IndexNAF.class);

	/* Based on assign function from EuroVocExecuter class */
	public File assign (File input, Properties properties) throws Exception {
		
		LOGGER.info("Create DocumentSet");
		File documentSet = File.createTempFile("EuroVocDocSet", "tmp");
		Utils.createDocumentSet(input, dict, documentSet);
		
		LOGGER.info("Assign input");
		cal.doAssigning(documentSet);
		
		LOGGER.info("Delete DocumentSet");
		documentSet.delete();

		return this.outFile;
	}
	
	
	
}
