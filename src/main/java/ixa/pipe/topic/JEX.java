package ixa.pipe.topic;

import tools.CreateCompactFormat;
import tools.EuroVoc;
import tools.PostProcess;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.TreeMap;
import java.util.Map;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.Topic;



public class JEX {

    private Properties properties;	
    private PreprocessNAF preprocess;
    private IndexNAF index;
    private PostprocessNAF post; 

    public JEX (String filename) throws Exception{
	InputStream input = null;
	try{
	    properties = new Properties();
	    input = new FileInputStream(filename);
	    properties.load(input);
	    preprocess = new PreprocessNAF(properties);
	    index = new IndexNAF(properties);
	    post = new PostprocessNAF(properties);
	} catch (IOException ex) {
	    ex.printStackTrace();
	} finally {
	    if (input != null) {
		try {
		    input.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	
    }

    public void getTopics(KAFDocument kaf) throws Exception{
	// create output file, tmp 
	File preDoc = preprocess.createPreprocess(kaf);
	File assignRes = index.assign(preDoc);
	post.postProcess(assignRes,kaf);
        //  // deletes files when the virtual machine terminate
        preDoc.deleteOnExit();
	assignRes.deleteOnExit();
    }
    
}
