package com.dah.desb.domain.route.runtime;

import java.util.ArrayList;
import java.util.List;

import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.route.dynamic.MulticastRouteEndpintCleanup;
import com.dah.desb.domain.route.dynamic.RouteEndpointHolder;
import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.endpoint.expression.ExchangeHolder;
import com.dah.desb.domain.route.endpoint.expression.ExpressionEvaluator;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogService;
import com.dah.desb.domain.route.endpoint.log.processor.RouteEndpointExceptionProcessor;
import com.dah.desb.domain.route.endpoint.log.processor.RouteEndpointLogBeginProcessor;
import com.dah.desb.domain.route.endpoint.log.processor.RouteEndpointLogEndProcessor;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.domain.route.log.RouteLogService;
import com.dah.desb.domain.route.log.processor.RouteLogProcessor;

@Component
public class RouteEndpointContext implements InitializingBean{

	private static final String DIRECT = "direct:";
	private static final String MULTICAST = "multicast";
	private static final String SEPARATOR = "_";

	@Autowired
	private RouteEndpointMapper routeEndpointMapper;

	@Autowired
	private RouteLogService routeLogService;

	@Autowired
	private RouteEndpointLogService routeEndpointLogService;

	@Autowired
	private ExpressionEvaluator exprissionEvaluator;

	@Autowired
	private CamelContext camelContext;

	@Autowired
	private EndpointMapperRegistry endpointMapperRegistry;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 启动camelContext
		camelContext.start();
	}

	public boolean loaded(String routeId) {
		for (RouteEndpoint routeEndpoint : routeEndpointMapper.getListByRoute(routeId)) {
			if (camelContext.getRoute(routeEndpoint.getId()) == null) {
				return false;
			}
		}
		return true;
	}

	public void loadRouteEndpoints(String routeId) throws RouteException {
		RouteEndpoint routeEndpointFrom = routeEndpointMapper.getFrom(routeId);
		if (routeEndpointFrom == null) {
			throw new RouteException("起始路由端点不存在");
		}
		Endpoint endpointFrom = endpointMapperRegistry.determineEndpoint(routeEndpointFrom);
		if (endpointFrom == null) {
			throw new RouteException("起始端点不存在");
		}
		routeEndpointFrom.setEndpointUrl(endpointFrom.buildUrl());
		// 卸载、加载起始路由端点
		unloadRouteEndpoint(routeEndpointFrom);
		loadRouteEndpointFrom(routeEndpointFrom);
		// 卸载、加载下级路由端点
		List<RouteEndpoint> routeEndpointNext = routeEndpointMapper.getNextList(routeEndpointFrom.getId());
		unloadRouteEndpoint(routeEndpointNext);
		loadRouteEndpointNext(routeEndpointNext);
	}

	private void loadRouteEndpointFrom(RouteEndpoint routeEndpointFrom) throws RouteException {
		try {
			final RouteEndpointContext routeEndpointContext = this;
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {

					// 异常处理
					onException(Exception.class).process(new RouteEndpointExceptionProcessor(routeEndpointFrom, routeEndpointLogService));

					from(routeEndpointFrom.getEndpointUrl()).routeId(routeEndpointFrom.getId()).autoStartup(false)

							// 【路由端点前】记录路由执行日志（整个路由的日志而非起始路由端点本身）
							.process(new RouteLogProcessor(routeEndpointFrom.getRouteId(), routeLogService))

							// 【路由端点前】标记开始时间
							.process(new RouteEndpointLogBeginProcessor())

							// 【路由端点后】记录路由端点执行日志
							.process(new RouteEndpointLogEndProcessor(routeEndpointFrom, routeEndpointLogService))

							// 【路由端点后】标记当前路由端点，doDirect基于此构件动态路由
							.process(new RouteEndpointHolder(routeEndpointFrom))

							// 【路由端点后】动态路由决策
							.dynamicRouter().method(routeEndpointContext, "doDirect")

							// 【路由端点后】卸载由doDirect生成并加载的广播路由端点（虚拟）
							.process(new MulticastRouteEndpintCleanup(routeEndpointContext, routeEndpointFrom));
				}
			});
		} catch (Exception e) {
			throw new RouteException("加载起始路由端点异常", e);
		}
	}

	private void loadRouteEndpointNext(List<RouteEndpoint> routeEndpoints) throws RouteException {
		for (RouteEndpoint routeEndpoint : routeEndpoints) {
			Endpoint endpoint = endpointMapperRegistry.determineEndpoint(routeEndpoint);
			if (endpoint == null) {
				throw new RouteException("端点不存在");
			}
			routeEndpoint.setEndpointUrl(endpoint.buildUrl());
			loadRouteEndpoint(routeEndpoint);
			loadRouteEndpointNext(routeEndpointMapper.getNextList(routeEndpoint.getId()));
		}
	}

	private void loadRouteEndpoint(RouteEndpoint routeEndpoint) throws RouteException {
		try {
			final RouteEndpointContext routeEndpointContext = this;
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {

					// 异常处理
					onException(Exception.class).process(new RouteEndpointExceptionProcessor(routeEndpoint, routeEndpointLogService));

					from(DIRECT + routeEndpoint.getId()).routeId(routeEndpoint.getId())

							// 【路由端点前】标记开始时间
							.process(new RouteEndpointLogBeginProcessor())

							// 【路由端点中】路由端点执行
							.to(routeEndpoint.getEndpointUrl())

							// 【路由端点后】记录路由端点执行日志
							.process(new RouteEndpointLogEndProcessor(routeEndpoint, routeEndpointLogService))

							// 【路由端点后】标记当前路由端点，doDirect基于此构件动态路由
							.process(new RouteEndpointHolder(routeEndpoint))

							// 【路由端点后】动态路由决策
							.dynamicRouter().method(routeEndpointContext, "doDirect")

							// 【路由端点后】卸载由doDirect生成并加载的广播路由端点（虚拟）
							.process(new MulticastRouteEndpintCleanup(routeEndpointContext, routeEndpoint));
				}
			});
		} catch (Exception e) {
			throw new RouteException("加载路由端点异常", e);
		}
	}

	public String doDirect(Exchange exchange) throws Exception {
		try {
			ExchangeHolder.set(exchange);
			RouteEndpoint currentRouteEndpoint = RouteEndpointHolder.get(exchange);
			List<RouteEndpoint> actualRouteEndpoints = filterActualRouteEndpoints(routeEndpointMapper.getNextList(currentRouteEndpoint.getId()));
			if (actualRouteEndpoints.size() > 0) {
				String routeLogId = RouteLogProcessor.getRouteLogId(exchange);
				String multicastRouteEndpintId = generateMulticastRouteEndpintId(routeLogId, currentRouteEndpoint.getId());
				buildMulticastRouteEndpint(multicastRouteEndpintId, actualRouteEndpoints);
				return DIRECT + multicastRouteEndpintId;
			} else {
				return null;
			}
		} finally {
			RouteEndpointHolder.remove(exchange);
			ExchangeHolder.remove();
		}
	}

	private List<RouteEndpoint> filterActualRouteEndpoints(List<RouteEndpoint> possibleRouteEndpoints) throws NoSuchMethodException {
		List<RouteEndpoint> actualRouteEndpoints = new ArrayList<>();
		if (possibleRouteEndpoints != null && possibleRouteEndpoints.size() > 0) {
			for (RouteEndpoint routeEndpoint : possibleRouteEndpoints) {
				String expression = routeEndpoint.getExpression();
				if (StringUtils.isEmpty(expression) || exprissionEvaluator.assertTrue(expression)) {
					actualRouteEndpoints.add(routeEndpoint);
				}
			}
		}
		return actualRouteEndpoints;
	}

	public String generateMulticastRouteEndpintId(String routeLogId, String previousRouteEndpointId) {
		return MULTICAST + SEPARATOR + routeLogId + SEPARATOR + previousRouteEndpointId;
	}

	private void buildMulticastRouteEndpint(String multicastRouteId, List<RouteEndpoint> routeEndpoints) throws Exception {
		String[] directRouteEndpoints = new String[routeEndpoints.size()];
		for (int i = 0; i < routeEndpoints.size(); i++) {
			directRouteEndpoints[i] = DIRECT + routeEndpoints.get(i).getId();
		}
		// 加载广播路由端点（虚拟）
		loadRouteEndpointMulticast(multicastRouteId, directRouteEndpoints);
	}

	private void loadRouteEndpointMulticast(String multicastRouteId, String[] directRouteEndpoints) throws Exception {
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(DIRECT + multicastRouteId).routeId(multicastRouteId).multicast().parallelProcessing(false).to(directRouteEndpoints);
			}
		});
	}

	public void startRouteEndpointFrom(String routeId) throws RouteException {
		RouteEndpoint from = routeEndpointMapper.getFrom(routeId);
		if (from == null) {
			throw new RouteException("起始路由端点不存在");
		}
		if (camelContext.getRoute(from.getId()) == null) {
			throw new RouteException("起始路由端点未加载");
		}
		try {
			camelContext.startRoute(from.getId());
		} catch (Exception e) {
			// 启动失败，将当前路由的所有端点信息从camelContext中移除
			unloadRouteEndpoints(routeId);
			throw new RouteException("启动起始路由端点异常", e);
		}
	}

	public void unloadRouteEndpoints(String routeId) throws RouteException {
		for (RouteEndpoint routeEndpoint : routeEndpointMapper.getListByRoute(routeId)) {
			unloadRouteEndpoint(routeEndpoint);
		}
	}

	private void unloadRouteEndpoint(List<RouteEndpoint> routeEndpoints) throws RouteException {
		for (RouteEndpoint routeEndpoint : routeEndpoints) {
			unloadRouteEndpoint(routeEndpoint);
		}
	}

	private void unloadRouteEndpoint(RouteEndpoint routeEndpoint) throws RouteException {
		unloadRouteEndpoint(routeEndpoint.getId());
	}

	public void unloadRouteEndpoint(String routeEndpointId) throws RouteException {
		try {
			if (!StringUtils.isEmpty(routeEndpointId) && camelContext.getRoute(routeEndpointId) != null) {
				camelContext.stopRoute(routeEndpointId);
				camelContext.removeRoute(routeEndpointId);
			}
		} catch (Exception e) {
			throw new RouteException("卸载路由端点异常", e);
		}
	}

}
