package com.dah.desb.domain.route.endpoint.log.processor;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLog;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogService;
import com.dah.desb.domain.route.log.processor.RouteLogProcessor;

public class RouteEndpointExceptionProcessor implements Processor {

	private RouteEndpoint routeEndpoint;

	private RouteEndpointLogService routeEndpointLogService;

	public RouteEndpointExceptionProcessor(RouteEndpoint routeEndpoint, RouteEndpointLogService routeEndpointLogService) {
		this.routeEndpoint = routeEndpoint;
		this.routeEndpointLogService = routeEndpointLogService;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		RouteEndpointLog exceptionLog = new RouteEndpointLog();
		exceptionLog.setRouteId(routeEndpoint.getRouteId());
		exceptionLog.setRouteLogId(RouteLogProcessor.getRouteLogId(exchange));
		exceptionLog.setEndpointId(routeEndpoint.getEndpointId());
		exceptionLog.setEndpointType(routeEndpoint.getEndpointType());
		exceptionLog.setBeginTime(RouteEndpointLogBeginProcessor.getBeginTime(exchange));
		exceptionLog.setEndTime(new Date());
		exceptionLog.setDuration(exceptionLog.getEndTime().getTime()-exceptionLog.getBeginTime().getTime());
		Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
		exceptionLog.setHasException(true);
		exceptionLog.setExceptionType(exception.getClass().getTypeName());
		exceptionLog.setExceptionMessage(exception.getMessage());
		routeEndpointLogService.save(exceptionLog);
	}

}
