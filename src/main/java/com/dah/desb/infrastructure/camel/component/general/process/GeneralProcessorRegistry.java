package com.dah.desb.infrastructure.camel.component.general.process;

import java.util.HashMap;
import java.util.Map;

public class GeneralProcessorRegistry {

	private Map<GeneralProcessType, GeneralProcessor> processors = new HashMap<>();

	public GeneralProcessorRegistry add(GeneralProcessType processType, GeneralProcessor processor) {
		processors.put(processType, processor);
		return this;
	}

	public GeneralProcessor getProcessor(GeneralProcessType processType) {
		return processors.get(processType);
	}

}
