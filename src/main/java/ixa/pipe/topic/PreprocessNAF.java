package ixa.pipe.topic;

import it.jrc.lt.evidx.Utils;
import tools.HTMLEntities;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import ixa.kaflib.KAFDocument;

/* Class based on the CreateCompactFormat class - tools - Eurovoc */
/* The functions names are different, the code is almost the same */ 

public class PreprocessNAF {

    /* Copied from CreateCompactFormat */
    private static Pattern multiWords = null;

    private static Pattern markUpTags = Pattern.compile("<.+?>");

    static Pattern url = Pattern.compile("(?:http://)?www\\..+\\b");
    static Pattern mail = Pattern.compile("\\S+?@\\S+");
    
    private static Pattern hyphenWithinWord = Pattern.compile("(\\p{L})-(\\p{L})");
    private static String hyphenReplacement = "BBBBBBBBBBBBBBB";
    private static Pattern not_letters = Pattern.compile("\\P{L}");
    private static Pattern numbers = Pattern.compile("\\d+");
    private static Pattern underScore = Pattern.compile("_");
    private static Pattern hyphenReplacementMatch = Pattern.compile(hyphenReplacement);
    static Pattern whiteSpace = Pattern.compile("\\s+");

    private static Properties properties = new Properties();
    private static int numberOfRemovedWords = 0;
    private static Set<String> stopWords = new HashSet<String>();

    public PreprocessNAF(final Properties p){
	properties = p;
	String stopWordListFile = properties.getProperty(Utils.STOP_WORDS_FILE);
	if (stopWordListFile != null)
	    stopWords = Utils.getStopWords(stopWordListFile);
    }

    /* Similar to createCompactFormat */
    /* differences: it works with one input file and just for indexing option */
    public static File createPreprocess (KAFDocument kaf) throws IOException
    {
	// create output file, tmp 
	File outFile = File.createTempFile("tmp", ".txt", new File("/tmp"));
	BufferedWriter compactFormatWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF8"));
	preProcessForIndexing(kaf, compactFormatWriter);
	
	compactFormatWriter.close();
	
	System.err.println("Finished creating compact format");
	return outFile;
    }


    /* Similar to the one at createCompactFormat */ 
    /* Instead of reading a list of Files, it just creates the input for one NAF file */ 
    private static void preProcessForIndexing(KAFDocument kaf, BufferedWriter compactFormatWriter)
    {
	String raw = kaf.getRawText();
	StringBuilder tmp = new StringBuilder();
	try{
	    String id = "docid";
	    if (raw != null){
		tmp.append(applyRegularExpressions(raw));
		writeCompactFormat(compactFormatWriter, tmp.toString(), id, null);
		numberOfRemovedWords = 0;
	    }
	} catch (IOException e) {
	    throw new RuntimeException("Could not create Compact format", e);
	}
    }
    
    
    /* Corrected function name; original: applyRegurlarExpressions */
    private static String applyRegularExpressions(String line)
    {
	Matcher m = markUpTags.matcher(HTMLEntities.unhtml(line.toLowerCase()));
	
	if (multiWords != null) {
	    m = multiWords.matcher(m.replaceAll(" "));
	    
	    while (m.find()) {
		String words = m.group();
		String[] tmp = words.split("\\s+");
		numberOfRemovedWords = numberOfRemovedWords + tmp.length;
		
	    }
	}
	
	
	m = url.matcher(m.replaceAll(" "));
	m = mail.matcher(m.replaceAll(" "));
	m = hyphenWithinWord.matcher(m.replaceAll(" "));
	
	m = not_letters.matcher(m.replaceAll("$1" + hyphenReplacement + "$2"));
	m = numbers.matcher(m.replaceAll(" "));
	m = underScore.matcher(m.replaceAll(" "));
	m = hyphenReplacementMatch.matcher(m.replaceAll(" "));
	m = whiteSpace.matcher(m.replaceAll("-"));
	
	return m.replaceAll(" ");
    }
    
    private static void writeCompactFormat(BufferedWriter writer, String documentText,
					   String documentId, String documentCategories) throws IOException
    {
	
	String[] tokens = documentText.split(" ");
	String[] trimmedTokens = new String[tokens.length];
	int numberOfTrimmedTokens = 0;
	for (String token : tokens) {
	    String trimmed = token.trim();
	    if (trimmed.isEmpty())
		continue;
	    if (stopWords.contains(trimmed))
		
		{
		    numberOfRemovedWords++;
		    continue;
		}
	    if (trimmed.length() == 1)
		continue;
	    trimmedTokens[numberOfTrimmedTokens++] = trimmed;
	}
	
	if (numberOfTrimmedTokens > 0) {
	    if (documentCategories != null)
		writer.write(documentCategories);
	    
	    writer.write("# ");
	    writer.write(documentId);
	    writer.write(" #" + numberOfRemovedWords);
	    writer.newLine();
	    
	    for (int i = 0; i < numberOfTrimmedTokens - 1; i++) {
		
		writer.write(trimmedTokens[i]);
		writer.write(" ");
		
	    }
	    
	    writer.write(trimmedTokens[numberOfTrimmedTokens - 1]);
	    writer.newLine();
	}
	
    }
    
}
