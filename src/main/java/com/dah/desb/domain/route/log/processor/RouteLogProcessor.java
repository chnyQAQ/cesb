package com.dah.desb.domain.route.log.processor;

import java.util.Date;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.dah.desb.domain.route.log.RouteLog;
import com.dah.desb.domain.route.log.RouteLogService;

public class RouteLogProcessor implements Processor {

	private static final String ROUTE_LOG_ID = "_routeLogId";

	private String routeId;

	private RouteLogService routeLogService;

	public RouteLogProcessor(String routeId, RouteLogService routeLogService) {
		this.routeId = routeId;
		this.routeLogService = routeLogService;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		String id = UUID.randomUUID().toString();
		exchange.getProperties().put(ROUTE_LOG_ID, id);
		RouteLog routeLog = new RouteLog();
		routeLog.setId(id);
		routeLog.setRouteId(routeId);
		// todo invoke(info)
		routeLog.setInvoke("");
		routeLog.setBeginTime(new Date());
		routeLogService.save(routeLog);
	}

	public static String getRouteLogId(Exchange exchange) {
		return (String) exchange.getProperties().get(ROUTE_LOG_ID);
	}

}
