package ixa.pipe.topic;
 
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.JDOMException;

import java.util.TreeMap;
import java.util.List;
import java.util.Collections;

import java.io.File;
import java.io.IOException;

public class ReadJEXOutput {
    SAXBuilder builder;
    public ReadJEXOutput () throws Exception{
	builder = new SAXBuilder();
    }

    public TreeMap<Float,String> getTopicsInfo(String file){
	TreeMap<Float,String> result = new TreeMap<Float,String>(Collections.reverseOrder());

	File xmlFile = new File(file);
	try {
	    Document document = (Document) builder.build(xmlFile);
	    Element rootNode = document.getRootElement();
	    List list = rootNode.getChildren("category");
	    for (int i = 0; i < list.size(); i++) {
		
		Element node = (Element) list.get(i);
		String label = node.getAttributeValue("label");
		Float weight = Float.parseFloat(node.getAttributeValue("weight"));
		result.put(weight,label);
	    }
	    
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (JDOMException e) {
	    e.printStackTrace();
	} finally{
	    return result;
	}
    }
    
}
