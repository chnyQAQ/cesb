package com.dah.desb.domain.route.endpoint.log.processor;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RouteEndpointLogBeginProcessor implements Processor {

	private static final String BEGIN_TIME = "_routeEndpointBeginTime";

	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getProperties().put(BEGIN_TIME, new Date());
	}

	public static Date getBeginTime(Exchange exchange) {
		return (Date) exchange.getProperties().get(BEGIN_TIME);
	}

}
