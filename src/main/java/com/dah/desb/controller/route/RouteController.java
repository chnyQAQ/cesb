package com.dah.desb.controller.route;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.exception.RouteException;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.RouteService;
import com.dah.desb.domain.route.runtime.RouteManager;
import com.dah.desb.infrastructure.mybatis.Pagination;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RouteController<V> {
	
	@Autowired
	private RouteService routeService;

	@Autowired
	private RouteMapper routeMapper;
	
	@Autowired
	private RouteManager routeManager;

	@Autowired
	private RouteEndpointMapper routeEndpointMapper;

	@Autowired
	private EndpointMapperRegistry endpointMapperRegistry;
	
	@GetMapping(path = "/routes", produces = "text/html")
	public String toPage() {
		return "/view/route/routes";
	}
	
	@GetMapping(path = "/routes/{id}", produces = "text/html")
	public String toDesignPage(@PathVariable("id") String id, Model model) {
		Route route = routeMapper.getById(id);
		route.setLoaded(routeManager.loaded(id));
		model.addAttribute("route", route);
		Map<String, Endpoint> endpointMap = new HashMap<>();
		try {
			for (RouteEndpoint routeEndpoint : routeEndpointMapper.getListByRoute(id)) {
				endpointMap.put(routeEndpoint.getEndpointId(), endpointMapperRegistry.determineEndpoint(routeEndpoint));
			}
		} catch (RouteException e) {
			e.printStackTrace();
		}
		model.addAttribute("endpointMap", endpointMap);
		return "/view/route/route";
	}

	@GetMapping(path = "/routes/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<Route> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
		Pagination<Route> pagination = new Pagination<>(routeMapper.getPage(search, new RowBounds(pageNum, pageSize)));
		if (pagination.getRows() != null) {
			for (Route route : pagination.getRows()) {
				route.setLoaded(routeManager.loaded(route.getId()));
			}
		}
		return pagination;
	}

	@PostMapping(path = "/routes", produces = "application/json")
	@ResponseBody
	public Route save(@RequestBody Route route) {
		return routeService.save(route);
	}
	
	@PutMapping(path = "/routes/{id}", produces = "application/json")
	@ResponseBody
	public Route update(@PathVariable("id") String id, @RequestBody Route route) {
		route.setId(id);
		return routeService.update(route);
	}

	@PutMapping(path = "/routes/{id}/design", produces = "application/json")
	@ResponseBody
	public Route design(@PathVariable("id") String id, @RequestBody Route route) {
		route.setId(id);
		return routeService.design(route);
	}
	
	@PutMapping(path = "/routes/{id}/loading", produces = "application/json")
	@ResponseBody
	public void loadRoute(@PathVariable("id") String id) {
		routeManager.startRoute(id);
	}
	
	@PutMapping(path = "/routes/{id}/unloading", produces = "application/json")
	@ResponseBody
	public void unloadRoute(@PathVariable("id") String id) {
		routeManager.stopRoute(id);
	}

	@DeleteMapping(path = "/routes/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		routeManager.stopRoute(id);
		routeService.remove(id);
	}
}
