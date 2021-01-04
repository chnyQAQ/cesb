package com.dah.desb.controller.endpoint.webservice;

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

import com.dah.desb.domain.endpoint.webservice.EndpointWebService;
import com.dah.desb.domain.endpoint.webservice.EndpointWebServiceMapper;
import com.dah.desb.domain.endpoint.webservice.EndpointWebServiceService;
import com.dah.desb.infrastructure.mybatis.Pagination;
import com.github.pagehelper.Page;

import java.util.List;

@Controller
public class EndpointWebServiceController {

	@Autowired
	private EndpointWebServiceService endpointWebServiceService;
	
	@Autowired
	private EndpointWebServiceMapper endpointWebServiceMapper;

	@Autowired
	private EndpointAction endpointAction;
	
	@GetMapping(path = "/webservices", produces = "text/html")
	public String toPage() {
		return "/view/endpoint/webservice/endpoint-webservices";
	}

	@PostMapping(path = "/webservices", produces = "application/json")
	@ResponseBody
	public EndpointWebService save(@RequestBody EndpointWebService endpointWebService) {
		return endpointWebServiceService.save(endpointWebService);
	}
	
	@PutMapping(path = "/webservices/{id}", produces = "application/json")
	@ResponseBody
	public EndpointWebService update(@PathVariable("id") String id, @RequestBody EndpointWebService endpointWebService) {
		endpointWebService.setId(id);
		EndpointWebService result = endpointWebServiceService.update(endpointWebService);
		endpointAction.restartRouteByEndpoint(EndpointType.webservice, id);
		return result;
	}
	
	@DeleteMapping(path = "/webservices/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		endpointWebServiceService.remove(id);
	}
	
	@GetMapping(path = "/webservices/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<EndpointWebService> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
	    Page<EndpointWebService> page = endpointWebServiceMapper.getPage(search, new RowBounds(pageNum, pageSize));
	    return new Pagination<EndpointWebService>(page);
	}
	
}
