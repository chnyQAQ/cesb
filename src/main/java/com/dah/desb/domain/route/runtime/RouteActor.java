package com.dah.desb.domain.route.runtime;

import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.exception.RouteException;

public class RouteActor {

	private Route route;

	private RouteEndpointContext routeEndpointContext;

	public RouteActor(Route route, RouteEndpointContext routeEndpointContext) {
		this.route = route;
		this.routeEndpointContext = routeEndpointContext;
	}

	public boolean loaded() {
		return routeEndpointContext.loaded(route.getId());
	}

	public void start() throws RouteException {
		if (!loaded()) {
			loadRouteEndpoints();
		}
		startRouteEndpointFrom();
	}

	public void stop() throws RouteException {
		if (loaded()) {
			unloadRouteEndpoints();
		}
	}

	private void loadRouteEndpoints() throws RouteException {
		routeEndpointContext.loadRouteEndpoints(route.getId());
	}

	private void unloadRouteEndpoints() throws RouteException {
		routeEndpointContext.unloadRouteEndpoints(route.getId());
	}

	private void startRouteEndpointFrom() throws RouteException {
		routeEndpointContext.startRouteEndpointFrom(route.getId());
	}

}
