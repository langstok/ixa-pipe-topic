package com.langstok.nlp.topic.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.langstok.nlp.topic.configuration.TopicProperties;

import ixa.kaflib.KAFDocument;
import ixa.pipe.topic.IndexNAF;
import ixa.pipe.topic.PostprocessNAF;
import ixa.pipe.topic.PreprocessNAF;

@Service
@EnableConfigurationProperties(TopicProperties.class)
public class IxaTopicService {

	private static final Logger LOGGER = Logger.getLogger(IxaTopicService.class);

	@Autowired
	private TopicProperties topicProperties;
	
	private Properties properties;
	
	private IndexNAF index;
	private PostprocessNAF postprocess;
	
	private static String VERSION = IxaTopicService.class.getPackage().getImplementationVersion();
	private static String COMMIT = IxaTopicService.class.getPackage().getSpecificationVersion();


	public KAFDocument transform(KAFDocument kaf){

		String lang = kaf.getLang();
		KAFDocument.LinguisticProcessor lp = kaf.addLinguisticProcessor("topics", "ixa-pipe-topic-" + lang, VERSION + "-" + COMMIT);
		lp.setBeginTimestamp();

		try { 
			getTopics(kaf);
		}
		catch (Exception e){
			LOGGER.error("ixa-pipe-topic failed: ", e);
		}
		finally{
			lp.setEndTimestamp();
		}

		return kaf;
	}

	
	
	public KAFDocument getTopics(KAFDocument kaf) throws Exception{

		// create output file, tmp 
		File preDoc = PreprocessNAF.createPreprocess(kaf);
		File assignRes = index.assign(preDoc, properties);
		postprocess.postProcess(assignRes, kaf);

		//  // deletes files when the virtual machine terminate
		preDoc.deleteOnExit();
		assignRes.deleteOnExit();
		
		return kaf;
	}
	
	
	@PostConstruct
	private void init(){
		
		try{
			this.properties = new Properties();
			this.properties.load(new FileInputStream(topicProperties.getJexPropertiesPath()));
			
			new PreprocessNAF(properties);
			this.index = new IndexNAF();
			this.postprocess = new PostprocessNAF(this.properties);
			
		} catch (IOException e) {
			LOGGER.error("IOException reading properties", e);
		} 
	}
	
	
}
