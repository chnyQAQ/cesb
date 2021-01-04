package com.dah.desb.domain.route.dynamic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.log.processor.RouteLogProcessor;
import com.dah.desb.domain.route.runtime.RouteEndpointContext;

public class MulticastRouteEndpintCleanup implements Processor {

	private RouteEndpointContext routeEndpointContext;
	private RouteEndpoint routeEndpoint;

	public MulticastRouteEndpintCleanup(RouteEndpointContext routeEndpointContext, RouteEndpoint routeEndpoint) {
		this.routeEndpointContext = routeEndpointContext;
		this.routeEndpoint = routeEndpoint;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		String routeLogId = RouteLogProcessor.getRouteLogId(exchange);
		String routeEndpointId = routeEndpoint.getId();
		String multicastRouteEndpintId = routeEndpointContext.generateMulticastRouteEndpintId(routeLogId, routeEndpointId);
		routeEndpointContext.unloadRouteEndpoint(multicastRouteEndpintId);
	}

}
