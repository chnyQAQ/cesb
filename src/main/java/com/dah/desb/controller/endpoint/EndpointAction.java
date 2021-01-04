package com.dah.desb.controller.endpoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.runtime.RouteManager;
import com.dah.desb.infrastructure.exception.AppException;

@Component
public class EndpointAction {

	private static final Logger logger = LoggerFactory.getLogger(EndpointAction.class);

	@Autowired
	private RouteEndpointMapper routeEndpointMapper;

	@Autowired
	private RouteMapper routeMapper;

	@Autowired
	private RouteManager routeManager;

	public void restartRouteByEndpoint(EndpointType endpointType, String endpointId) {
		restartRoute(findRoutesByEndpoint(endpointType, endpointId));
	}

	private void restartRoute(List<Route> routes) {
		List<String> errorCodes = new ArrayList<>();
		for (Route route : routes) {
			try {
				routeManager.stopRoute(route.getId());
				routeManager.startRoute(route.getId());
			} catch (Exception e) {
				logger.error("重启路由失败，code=" + route.getCode(), e);
				errorCodes.add(route.getCode());
			}
		}
		if (errorCodes.size() > 0) {
			throw new AppException("自动重启路由失败，请尝试手动重启路由。\n" + String.join(",", errorCodes));
		}
	}

	private List<Route> findRoutesByEndpoint(EndpointType endpointType, String endpointId) {
		Set<String> routeIds = new HashSet<>();
		List<Route> routes = new ArrayList<>();
		for (RouteEndpoint routeEndpoint : routeEndpointMapper.getListByEndpoint(endpointType.toString(), endpointId)) {
			if (!routeIds.contains(routeEndpoint.getRouteId())) {
				Route route = routeMapper.getById(routeEndpoint.getRouteId());
				if (route != null) {
					routeIds.add(routeEndpoint.getRouteId());
					routes.add(route);
				}
			}
		}
		return routes;
	}

}
