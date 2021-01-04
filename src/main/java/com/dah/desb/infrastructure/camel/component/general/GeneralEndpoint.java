package com.dah.desb.infrastructure.camel.component.general;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.component.log.LogEndpoint;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;

import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessType;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessorRegistry;

import java.util.Map;

public class GeneralEndpoint extends DefaultEndpoint {

	private GeneralProcessorRegistry registry;

	private String key;

	@UriParam
	private GeneralProcessType processType;

	public GeneralEndpoint(String endpointUri, Component component, GeneralProcessorRegistry registry) {
		super(endpointUri, component);
		this.registry = registry;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new GeneralProducer(this, registry);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new GeneralConsumer(this, processor);
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public GeneralProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(GeneralProcessType processType) {
		this.processType = processType;
	}

	public GeneralProcessorRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(GeneralProcessorRegistry registry) {
		this.registry = registry;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
