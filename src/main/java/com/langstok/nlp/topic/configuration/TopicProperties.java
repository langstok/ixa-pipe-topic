package com.langstok.nlp.topic.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="ixatopic")
public class TopicProperties {
	
	private String jexPropertiesPath = "./default.prop";

	public String getJexPropertiesPath() {
		return jexPropertiesPath;
	}

	public void setJexPropertiesPath(String jexPropertiesPath) {
		this.jexPropertiesPath = jexPropertiesPath;
	}

}
