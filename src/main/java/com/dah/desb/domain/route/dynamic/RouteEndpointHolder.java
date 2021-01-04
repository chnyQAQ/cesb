package com.dah.desb.domain.route.dynamic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;

public class RouteEndpointHolder implements Processor {

	private static final String CURRENT_ROUTE_ENDPOINT = "_currentRouteEndpoint";

	private RouteEndpoint currentRouteEndpoint;

	public RouteEndpointHolder(RouteEndpoint currentRouteEndpoint) {
		this.currentRouteEndpoint = currentRouteEndpoint;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getProperties().put(CURRENT_ROUTE_ENDPOINT, currentRouteEndpoint);
	}

	public static RouteEndpoint get(Exchange exchange) {
		return (RouteEndpoint) exchange.getProperties().get(CURRENT_ROUTE_ENDPOINT);
	}

	public static void remove(Exchange exchange) {
		exchange.getProperties().remove(CURRENT_ROUTE_ENDPOINT);
	}

}
