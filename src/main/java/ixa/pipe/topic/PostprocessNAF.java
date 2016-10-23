package ixa.pipe.topic;

import java.io.File;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.jrc.lt.evidx.ThesaurusInfo;
import it.jrc.lt.evidx.Utils;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Topic;


public class PostprocessNAF {

	private static final Logger LOGGER = Logger.getLogger(PostprocessNAF.class);

	private static Properties properties = new Properties();
	private static ThesaurusInfo info;
	private final static String DISPLAY_LANG = "DisplayLanguage";

	public PostprocessNAF(final Properties p){
		PostprocessNAF.properties = p;
	}

	public KAFDocument postProcess (File input, KAFDocument kaf) throws Exception{
		String lang = kaf.getLang();
		String name = "ixa-pipe-topic-" + lang;

		String displayLang = properties.getProperty(DISPLAY_LANG);
		info = new ThesaurusInfo(new File(properties.getProperty(Utils.THESAURUS_INFO)), displayLang);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(input);
		NodeList assignedDocuments = doc.getElementsByTagName("document");

		LOGGER.info("Start postproccessing document");
		Element assignedDocument = (Element) assignedDocuments.item(0);
		String inputDoc = assignedDocument.getAttribute("id");
		NodeList categories = assignedDocument.getElementsByTagName("category");

		for (int i = 0; i < categories.getLength(); i++) {
			Element category = (Element) categories.item(i);
			String categoryCode = category.getAttribute("code");
			Float weight = Float.parseFloat(category.getAttribute("weight"));
			String label = info.getDescriptorLabel(categoryCode);
			Topic topic = kaf.newTopic(label);
			topic.setSource(name);
			topic.setMethod("JEX");
			topic.setConfidence(weight);
		}

		return kaf;
	}
}
