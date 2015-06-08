package ixa.pipe.topic;

import it.jrc.lt.evidx.*;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Topic;
import java.util.Properties;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class PostprocessNAF {
    private static Properties properties = new Properties();
    private static ThesaurusInfo info;
    private final static String DISPLAY_LANG = "DisplayLanguage";

    public PostprocessNAF(final Properties p){
	properties = p;
    }

    public void postProcess (File input,KAFDocument output) throws Exception{
	String lang = output.getLang();
	String name = "ixa-pipe-topic-" + lang;

	String displayLang = properties.getProperty(DISPLAY_LANG);
	info = new ThesaurusInfo(new File(properties.getProperty(Utils.THESAURUS_INFO)), displayLang);

	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document doc = builder.parse(input);
	NodeList assignedDocuments = doc.getElementsByTagName("document");

	System.err.println("Start postproccessing document");;
	Element assignedDocument = (Element) assignedDocuments.item(0);
	String inputDoc = assignedDocument.getAttribute("id");
	NodeList categories = assignedDocument.getElementsByTagName("category");

	for (int i = 0; i < categories.getLength(); i++) {
	    Element category = (Element) categories.item(i);
	    String categoryCode = category.getAttribute("code");
	    Float weight = Float.parseFloat(category.getAttribute("weight"));
	    String label = info.getDescriptorLabel(categoryCode);
	    Topic topic = output.newTopic(label,weight,name,"JEX");
	}
	
    }
}
