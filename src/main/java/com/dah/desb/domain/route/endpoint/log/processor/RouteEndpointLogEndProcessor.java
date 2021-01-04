package com.dah.desb.domain.route.endpoint.log.processor;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLog;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogService;
import com.dah.desb.domain.route.log.processor.RouteLogProcessor;

public class RouteEndpointLogEndProcessor implements Processor {

	private RouteEndpoint routeEndpoint;

	private RouteEndpointLogService routeEndpointLogService;

	public RouteEndpointLogEndProcessor(RouteEndpoint routeEndpoint, RouteEndpointLogService routeEndpointLogService) {
		this.routeEndpoint = routeEndpoint;
		this.routeEndpointLogService = routeEndpointLogService;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		RouteEndpointLog log = new RouteEndpointLog();
		log.setRouteId(routeEndpoint.getRouteId());
		log.setRouteLogId(RouteLogProcessor.getRouteLogId(exchange));
		log.setEndpointId(routeEndpoint.getEndpointId());
		log.setEndpointType(routeEndpoint.getEndpointType());
		log.setBeginTime(RouteEndpointLogBeginProcessor.getBeginTime(exchange));
		log.setEndTime(new Date());
		log.setDuration(log.getEndTime().getTime()-log.getBeginTime().getTime());
		log.setHasException(false);
		log.setExceptionType("");
		log.setExceptionMessage("");
		routeEndpointLogService.save(log);
	}

}
