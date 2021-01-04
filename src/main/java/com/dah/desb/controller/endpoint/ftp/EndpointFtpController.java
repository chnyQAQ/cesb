package com.dah.desb.controller.endpoint.ftp;

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

import com.dah.desb.domain.endpoint.ftp.EndpointFtp;
import com.dah.desb.domain.endpoint.ftp.EndpointFtpMapper;
import com.dah.desb.domain.endpoint.ftp.EndpointFtpService;
import com.dah.desb.infrastructure.mybatis.Pagination;
import com.github.pagehelper.Page;

import java.util.List;

@Controller
public class EndpointFtpController {

	@Autowired
	private EndpointFtpService endpointFtpService;
	
	@Autowired
	private EndpointFtpMapper endpointFtpMapper;

	@Autowired
	private EndpointAction endpointAction;
	
	@GetMapping(path = "/ftps", produces = "text/html")
	public String toPage() {
		return "/view/endpoint/ftp/endpoint-ftps";
	}

	@PostMapping(path = "/ftps", produces = "application/json")
	@ResponseBody
	public EndpointFtp save(@RequestBody EndpointFtp endpointFtp) {
		return endpointFtpService.save(endpointFtp);
	}
	
	@PutMapping(path = "/ftps/{id}", produces = "application/json")
	@ResponseBody
	public EndpointFtp update(@PathVariable("id") String id, @RequestBody EndpointFtp endpointFtp) {
		endpointFtp.setId(id);
		EndpointFtp result = endpointFtpService.update(endpointFtp);
		endpointAction.restartRouteByEndpoint(EndpointType.ftp, id);
		return result;
	}
	
	@DeleteMapping(path = "/ftps/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		endpointFtpService.remove(id);
	}
	
	@GetMapping(path = "/ftps/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<EndpointFtp> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
	    Page<EndpointFtp> page = endpointFtpMapper.getPage(search, new RowBounds(pageNum, pageSize));
	    return new Pagination<EndpointFtp>(page);
	}
	
}
