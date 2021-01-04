package com.dah.desb.infrastructure.camel.component.general;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessType;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessor;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessorRegistry;

public class GeneralProducer extends DefaultProducer {

	private GeneralProcessorRegistry registry;

	public GeneralProducer(Endpoint endpoint, GeneralProcessorRegistry registry) {
		super(endpoint);
		this.registry = registry;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		GeneralEndpoint endpoint = (GeneralEndpoint) getEndpoint();
		GeneralProcessor processor = registry.getProcessor(endpoint.getProcessType());
		processor.process(endpoint, exchange);
	}

}
