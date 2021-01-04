package com.dah.desb.domain.route.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.infrastructure.exception.AppException;

@Component
public class RouteManager implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(RouteManager.class);

	private Map<String, RouteActor> routeActors = new ConcurrentHashMap<>();

	@Autowired
	private RouteMapper routeMapper;

	@Autowired
	private RouteEndpointContext routeEndpointContext;

	@Override
	public void afterPropertiesSet() throws InterruptedException {
		for (Route route : routeMapper.getAll()) {
			try {
				startRoute(route);
			} catch (Exception e) {
				logger.error("启动路由失败 - " + route.getId(), e);
			}
		}
	}

	public void startRoute(String routeId) {
		if (StringUtils.isEmpty(routeId)) {
			throw new AppException("路由Id不能为空");
		}
		startRoute(routeMapper.getById(routeId));
	}

	private void startRoute(Route route) {
		if (route == null) {
			throw new AppException("路由不存在");
		}
		if (!route.isEnabled()) {
			throw new AppException("路由未启用");
		}
		RouteActor routeActor = routeActors.get(route.getId());
		if (routeActor == null) {
			routeActor = new RouteActor(route, routeEndpointContext);
			routeActors.put(route.getId(), routeActor);
		}
		try {
			routeActor.start();
		} catch (RouteException e) {
			throw new AppException("启动路由失败 - " + e.getMessage(), e);
		}
	}

	@Override
	public void destroy() throws Exception {
		for (String routeId : routeActors.keySet()) {
			try {
				stopRoute(routeActors.remove(routeId));
			} catch (Exception e) {
				logger.error("停止路由失败 - " + routeId, e);
			}
		}
	}

	public void stopRoute(String routeId) {
		if (StringUtils.isEmpty(routeId)) {
			throw new AppException("路由Id不能为空");
		}
		RouteActor routeActor = routeActors.get(routeId);
		try {
			stopRoute(routeActor);
			routeActors.remove(routeId);
		} catch (RouteException e) {
			throw new AppException("停止路由失败 - " + e.getMessage(), e);
		}
	}

	private void stopRoute(RouteActor routeActor) throws RouteException {
		if (routeActor != null) {
			routeActor.stop();
		}
	}

	public boolean loaded(String routeId) {
		if (routeActors.get(routeId) != null) {
			return routeActors.get(routeId).loaded();
		} else {
			return false;
		}
	}

	public boolean updateRouteEndpoint(Endpoint Endpoint) {

		return true;
	}

}
