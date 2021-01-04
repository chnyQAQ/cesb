package com.dah.desb.controller.endpoint.http;

import com.dah.desb.controller.endpoint.EndpointAction;
import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointType;
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

import com.dah.desb.domain.endpoint.http.EndpointHttp;
import com.dah.desb.domain.endpoint.http.EndpointHttpMapper;
import com.dah.desb.domain.endpoint.http.EndpointHttpService;
import com.dah.desb.infrastructure.mybatis.Pagination;
import com.github.pagehelper.Page;

import java.util.List;

@Controller
public class EndpointHttpController {

	@Autowired
	private EndpointHttpService endpointHttpService;
	
	@Autowired
	private EndpointHttpMapper endpointHttpMapper;

	@Autowired
	private EndpointAction endpointAction;
	
	@GetMapping(path = "/https", produces = "text/html")
	public String toPage() {
		return "/view/endpoint/http/endpoint-https";
	}

	@PostMapping(path = "/https", produces = "application/json")
	@ResponseBody
	public EndpointHttp save(@RequestBody EndpointHttp endpointHttp) {
		return endpointHttpService.save(endpointHttp);
	}
	
	@PutMapping(path = "/https/{id}", produces = "application/json")
	@ResponseBody
	public EndpointHttp update(@PathVariable("id") String id, @RequestBody EndpointHttp endpointHttp) {
		endpointHttp.setId(id);
		EndpointHttp result = endpointHttpService.update(endpointHttp);
		endpointAction.restartRouteByEndpoint(EndpointType.http, id);
		return result;
	}
	
	@DeleteMapping(path = "/https/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		endpointHttpService.remove(id);
	}
	
	@GetMapping(path = "/https/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<EndpointHttp> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
	    Page<EndpointHttp> page = endpointHttpMapper.getPage(search, new RowBounds(pageNum, pageSize));
	    return new Pagination<EndpointHttp>(page);
	}
	
}
