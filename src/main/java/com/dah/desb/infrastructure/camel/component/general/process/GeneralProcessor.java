package com.dah.desb.infrastructure.camel.component.general.process;

import com.dah.desb.infrastructure.camel.component.general.GeneralEndpoint;
import org.apache.camel.Exchange;

public interface GeneralProcessor {
	
	void process(GeneralEndpoint endpoint, Exchange exchange);

}
