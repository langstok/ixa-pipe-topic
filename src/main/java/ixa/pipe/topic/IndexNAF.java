package ixa.pipe.topic;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import it.jrc.lt.evidx.CalculateClassifiers;
import it.jrc.lt.evidx.Dictionary;
import it.jrc.lt.evidx.Utils;

@Service
public class IndexNAF {

	private static final Logger LOGGER = Logger.getLogger(IndexNAF.class);

	/* Based on assign function from EuroVocExecuter class */
	public File assign (File input, Properties properties) throws Exception {
		LOGGER.info("start indexing document");
		
		File outFile = File.createTempFile("assignTmp", ".xml", new File("/tmp"));
		properties.setProperty("output", outFile.getAbsolutePath());
		
		CalculateClassifiers cal = new CalculateClassifiers(properties);
		LOGGER.info("Building Classifier done.");

		Dictionary dict = new Dictionary(properties.getProperty(Utils.DICTIONARY));
		dict.load();
		
		File documentSet = File.createTempFile("EuroVocDocSet", "tmp");
		documentSet.deleteOnExit();

		Utils.createDocumentSet(input, dict, documentSet);
		cal.doAssigning(documentSet);
		LOGGER.info("Building Classifier done.");
		return outFile;
	}
	
}
