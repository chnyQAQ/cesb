package com.dah.desb.controller.endpoint.rest;

import com.dah.desb.controller.endpoint.EndpointAction;
import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.runtime.RouteManager;
import com.dah.desb.infrastructure.exception.AppException;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dah.desb.domain.endpoint.rest.EndpointRest;
import com.dah.desb.domain.endpoint.rest.EndpointRestMapper;
import com.dah.desb.domain.endpoint.rest.EndpointRestService;
import com.dah.desb.infrastructure.mybatis.Pagination;
import com.github.pagehelper.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class EndpointRestController {

	@Autowired
	private EndpointRestService endpointRestService;
	
	@Autowired
	private EndpointRestMapper endpointRestMapper;

	@Autowired
	private EndpointAction endpointAction;

	@GetMapping(path = "/rests", produces = "text/html")
	public String toPage() {
		return "/view/endpoint/rest/endpoint-rests";
	}

	@PostMapping(path = "/rests", produces = "application/json")
	@ResponseBody
	public EndpointRest save(@RequestBody EndpointRest endpointRest) {
		return endpointRestService.save(endpointRest);
	}
	
	@PutMapping(path = "/rests/{id}", produces = "application/json")
	@ResponseBody
	public EndpointRest update(@PathVariable("id") String id, @RequestBody EndpointRest endpointRest) {
		endpointRest.setId(id);
		EndpointRest result = endpointRestService.update(endpointRest);
		endpointAction.restartRouteByEndpoint(EndpointType.rest, id);
		return result;
	}
	
	@DeleteMapping(path = "/rests/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		endpointRestService.remove(id);
	}
	
	@GetMapping(path = "/rests/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<EndpointRest> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
	    Page<EndpointRest> page = endpointRestMapper.getPage(search, new RowBounds(pageNum, pageSize));
	    return new Pagination<EndpointRest>(page);
	}
	
}
