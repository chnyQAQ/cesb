package com.dah.desb.infrastructure.camel.component.general;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessorRegistry;

public class GeneralComponent extends DefaultComponent {

	private GeneralProcessorRegistry registry;
	
	public GeneralComponent(GeneralProcessorRegistry registry) {
		this.registry = registry;
	}

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		return new GeneralEndpoint(uri, this, registry);
	}

}
