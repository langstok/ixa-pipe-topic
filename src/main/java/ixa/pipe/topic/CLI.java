package ixa.pipe.topic;

//import java.io.IOException;
//import org.jdom2.JDOMException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import ixa.kaflib.KAFDocument;


public class CLI {

    public static void main(String[] args) throws Exception {

	Namespace parsedArguments = null;
	
        // create Argument Parser
        ArgumentParser parser = ArgumentParsers.newArgumentParser(
            "ixa-pipe-topic-1.0.0jar").description(
            "ixa-pipe-topic-1.0.0 is a topic detection module "
                + "developed by IXA NLP Group based on JEX.\n");

	// parser.addArgument("-f", "--file").help("the file which contains the file names to be processed")
	//     .required(true);

	parser.addArgument("-p", "--properties").help("the properties file")
	    .required(true);
        
        /*
         * Parse the command line arguments
         */

        // catch errors and print help
        try {
          parsedArguments = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
          parser.handleError(e);
          System.out
              .println("Run java -jar target/ixa-pipe-topic.1.0.0.jar -help for details");
          System.exit(1);
        }

	String prop = parsedArguments.getString("properties");

	// Input
	BufferedReader stdInReader = null;
	// Output
	BufferedWriter w = null;
		
	stdInReader = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
	w = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
	KAFDocument kaf = KAFDocument.createFromStream(stdInReader);
		
	String version = CLI.class.getPackage().getImplementationVersion();
	String commit = CLI.class.getPackage().getSpecificationVersion();
	String lang = kaf.getLang();
		
	KAFDocument.LinguisticProcessor lp = kaf.addLinguisticProcessor("topics", "ixa-pipe-topic-" + lang, version + "-" + commit);
	lp.setBeginTimestamp();
		
	try { 
	    JEX jex = new JEX(prop);
	    jex.getTopics(kaf);
	}
	catch (Exception e){
	    System.err.println("ixa-pipe-topic failed: ");
	    e.printStackTrace();
	}
	finally {
	    lp.setEndTimestamp();
	    w.write(kaf.toString());
	    w.close();
	}

    }
}
