package ixa.pipe.topic;

import java.io.IOException;
import org.jdom2.JDOMException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CLI {

    public static void main(String[] args) throws Exception {

	Namespace parsedArguments = null;
	
        // create Argument Parser
        ArgumentParser parser = ArgumentParsers.newArgumentParser(
            "ixa-pipe-topic-1.0.0jar").description(
            "ixa-pipe-topic-1.0.0 is a topic detection module "
                + "developed by IXA NLP Group based on JEX.\n");

	parser.addArgument("-f", "--file").help("the file which contains the file names to be processed")
	    .required(true);

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


	String input = parsedArguments.getString("file");
	String prop = parsedArguments.getString("properties");

	JEX jex = new JEX(prop);
	jex.getTopics(input);
	jex.clean();
    }
}
