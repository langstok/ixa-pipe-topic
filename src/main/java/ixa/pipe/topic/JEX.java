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

    private Path rootDir = null;
    private CreateCompactFormat preprocess;
    private EuroVoc index;
    private PostProcess post;
    private ReadJEXOutput jex;

    private Properties prop;
    private String preProp = "PreProcess.properties";
    private String indexProp = "Index.properties";
    private String postProp = "Postprocess.properties";

    private String inputPre = null;
    private String outputPre = null;
    private String outputInd = null;
    private String outputPost = null;
	

    public JEX(String prop) throws Exception{
	rootDir = Files.createTempDirectory(null);
	initProperties(prop);
	preprocess = new CreateCompactFormat();
	index = new EuroVoc();
	post = new PostProcess();
	jex = new ReadJEXOutput();
	
    }

    private void initProperties(String filename){
	InputStream input = null;
	try{
	    prop = new Properties();
	    input = new FileInputStream(filename);
	    prop.load(input);
	    createPreprocessProp();
	    createIndexProp();
	    createPostprocessProp();
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

	
    private void createPreprocessProp(){
	try{
	    String inputDir = prop.getProperty("inputDir");
	    String stopwords = prop.getProperty("stopwords");
	    String output = prop.getProperty("outputPre");
	    String acceptedF = prop.getProperty("AcceptedFormates");
	    String multiwords = prop.getProperty("MultiWordsFile");
	    
	    String rootStr = rootDir.toString();
	    
	    File file = new File(rootStr+"/"+preProp);
	    
	    BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
	    bw.write("inputDir="+rootStr+"/"+inputDir+"\n");
	    bw.write("stopwords="+stopwords+"\n");
	    bw.write("output="+rootStr+"/"+output+"\n");
	    bw.write("AcceptedFormates="+acceptedF+"\n");
	    bw.write("MultiWordsFile="+multiwords+"\n");
	    bw.close();
	    
	    inputPre = rootStr+"/"+inputDir;
	    if (!Files.isDirectory(Paths.get(inputPre))) {
		Files.createDirectories(Paths.get(inputPre));
	    }
	    outputPre = rootStr+"/"+output;
	    String[] dirs = output.split("/");
	    String dir = "";
	    for (int i=0; i<dirs.length-1; i++){
		dir += dirs[i] + "/";
	    }
	    if (!Files.isDirectory(Paths.get(rootStr+"/"+dir))) {
		Files.createDirectories(Paths.get(rootStr+"/"+dir));
	    }
	    
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
	
    private void createIndexProp(){
	try{
	    String input = prop.getProperty("inputIndex");
	    String output = prop.getProperty("outputIndex");
	    String blacklist = prop.getProperty("blacklist");
	    String classifiersDir = prop.getProperty("classifiersDir");
	    String dict = prop.getProperty("dict");
	    String minNumDesc = prop.getProperty("minNumDesc");
	    String rank = prop.getProperty("rank");
	    String minNumCommonTokens = prop.getProperty("minNumCommonTokens");

	    String rootStr = rootDir.toString();
	    
	    File file = new File(rootStr+"/"+indexProp);
	    
	    BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
	    bw.write("input="+rootStr+"/"+input+"\n");
	    bw.write("output="+rootStr+"/"+output+"\n");
	    bw.write("blacklist="+blacklist+"\n");
	    bw.write("classifiersDir="+classifiersDir+"\n");
	    bw.write("dict="+dict+"\n");
	    bw.write("minNumDesc="+minNumDesc+"\n");
	    bw.write("rank="+rank+"\n");
	    bw.write("minNumCommonTokens="+minNumCommonTokens+"\n");
	    
	    bw.close();
	    
	    outputInd = rootStr+"/"+output;
	    String[] dirs = output.split("/");
	    String dir = "";
	    for (int i=0; i<dirs.length-1; i++){
		dir += dirs[i] + "/";
	    }
	    if (!Files.isDirectory(Paths.get(rootStr+"/"+dir))) {
		Files.createDirectories(Paths.get(rootStr+"/"+dir));
	    }
	    
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
	
    private void createPostprocessProp(){
	try{
	    String ThesaurusInfo = prop.getProperty("ThesaurusInfo");
	    String DisplayLanguage = prop.getProperty("DisplayLanguage");
	    String classifiersDir = prop.getProperty("classifiersDir");
	    String documents = prop.getProperty("documents");
	    String input = prop.getProperty("inputPost");
	    String appendResult = prop.getProperty("appendResult");
	    String showDescriptorAssociates = prop.getProperty("showDescriptorAssociates");
	    String showBroaderTerms = prop.getProperty("showBroaderTerms");
	    String showRelatedTerms = prop.getProperty("showRelatedTerms");
	    String showMicroThesaurus = prop.getProperty("showMicroThesaurus");
	    String resultDir = prop.getProperty("resultDirPost");
	    
	    String rootStr = rootDir.toString();

	    File file = new File(rootStr+"/"+postProp);
	    
	    BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
	    bw.write("ThesaurusInfo="+ThesaurusInfo+"\n");
	    bw.write("DisplayLanguage="+DisplayLanguage+"\n");
	    bw.write("classifiersDir="+classifiersDir+"\n");
	    bw.write("documents="+rootStr+"/"+documents+"\n");
	    bw.write("input="+rootStr+"/"+input+"\n");
	    bw.write("appendResult="+appendResult+"\n");
	    bw.write("showDescriptorAssociates="+showDescriptorAssociates+"\n");
	    bw.write("showBroaderTerms="+showBroaderTerms+"\n");
	    bw.write("showRelatedTerms="+showRelatedTerms+"\n");
	    bw.write("showMicroThesaurus="+showMicroThesaurus+"\n");
	    bw.write("resultDir="+rootStr+"/"+resultDir+"\n");
	    bw.close();

	    outputPost = rootStr+"/"+resultDir;
	    if (!Files.isDirectory(Paths.get(outputPost))) {
		Files.createDirectories(Paths.get(outputPost));
	    }
	    
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    public String[] getPreprocessProp(){
	String[] argsPre = new String[1];
	argsPre[0] = rootDir.toString() + "/" + preProp;
	return argsPre;
    }
    
    public String[] getIndexProp(){
	String[] argsInd = new String[1];
	argsInd[0] = rootDir.toString() + "/" + indexProp;
	return argsInd;
    }
    
    public String[] getPostProcessProp(){
	String[] argsPost = new String[1];
	argsPost[0] = rootDir.toString() + "/" + postProp;
	return argsPost;
    }
    
    public String getPreprocessInputDir(){
	return inputPre;
    }
    
    public String getPostProcessOutputDir(){
	return outputPost;
    }

    public void getTopics(String input) throws Exception{

	// prepare the documents
	createInput(input);
	// preprocess the documents
	preprocess();
	// index the documents
	index();
	// postprocess the documents
	postprocess();
	// for each document, add the topic info
	createOutput(input);
    }

    private void createInput(String input){
	BufferedReader br = null;
	try {
	    String dir = getPreprocessInputDir();
	    String kafFile;
	    String rootStr = rootDir.toString();
	    br = new BufferedReader(new FileReader(input));
	    while ((kafFile = br.readLine()) != null) {
		// get the text of the document
		KAFDocument kaf = KAFDocument.createFromFile(new File(kafFile));

		String lang = kaf.getLang();
		KAFDocument.LinguisticProcessor lp = kaf.addLinguisticProcessor("topics", "ixa-pipe-topic-" + lang, "1.0.0");
		lp.setBeginTimestamp();
		kaf.save(kafFile);

		String text = kaf.getRawText();
		String[] path = kafFile.split("/");
		String newFileName = dir + "/" + path[path.length-1] + ".txt";
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFileName), "UTF8"));
		w.write(text);
		w.close();
	    }
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (br != null)br.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}
    }

    private void preprocess() throws Exception{
	preprocess.main(getPreprocessProp());
    }

    private void index() throws Exception{
	index.main(getIndexProp());
    }

    private void postprocess() throws Exception{
	post.main(getPostProcessProp());
    }


    private void createOutput(String output){
	BufferedReader br = null;
	try {
	    String dir = getPostProcessOutputDir();
	    String kafFile;
	    br = new BufferedReader(new FileReader(output));
	    while ((kafFile = br.readLine()) != null) {
		KAFDocument kaf = KAFDocument.createFromFile(new File(kafFile));
		String lang = kaf.getLang();
		String name = "ixa-pipe-topic-" + lang;

		Map<String, List<KAFDocument.LinguisticProcessor>> lps = kaf.getLinguisticProcessors();
		List<KAFDocument.LinguisticProcessor> topicLp = lps.get("topics");
		for (KAFDocument.LinguisticProcessor lp: topicLp){
		    if (lp.getName().equals(name)){
			lp.setEndTimestamp();
		    }
		}

		String[] path = kafFile.split("/");
		String topicFile = dir + "/" + path[path.length-1] + ".txt";
		TreeMap<Float,String> topics = getTopicsInfo(topicFile);
		for(Float weight: topics.keySet()){
		    String value = topics.get(weight);
		    Topic topic = kaf.newTopic(value,weight,name,"JEX");
		}
		kaf.save(kafFile);
	    }
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (br != null)br.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}
    }

    private TreeMap<Float,String> getTopicsInfo(String file){
	TreeMap<Float,String> result = jex.getTopicsInfo(file);
	return result;
    }

    public void clean(){
	deleteDirOnExit(rootDir.toFile());
    }

    private void deleteDirOnExit(File dir)  {  
	dir.deleteOnExit();  
	File[] files = dir.listFiles();  
	if (files != null) {  
	    for (File f: files) {  
		if (f.isDirectory()) {  
		    deleteDirOnExit(f);  
		}  
		else{  
		    f.deleteOnExit();  
		}  
	    }  
	}  
    }  
    
}
