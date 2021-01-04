package com.dah.desb.infrastructure.camel.component.general;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class GeneralConsumer extends DefaultConsumer {

	public GeneralConsumer(Endpoint endpoint, Processor processor) {
		super(endpoint, processor);
	}

}
