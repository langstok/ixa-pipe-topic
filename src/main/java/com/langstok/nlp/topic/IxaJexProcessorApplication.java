package com.langstok.nlp.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

import com.langstok.nlp.topic.service.IxaJexTopicService;

import ixa.kaflib.KAFDocument;

@SpringBootApplication
@EnableBinding(Processor.class)
public class IxaJexProcessorApplication {

	@Autowired
	private IxaJexTopicService service;
	
	public static void main(String[] args) {
		SpringApplication.run(IxaJexProcessorApplication.class, args);
	}
	
	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public KAFDocument handle(KAFDocument kaf) {
      return service.transform(kaf);
    }
}
