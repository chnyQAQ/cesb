package com.dah.desb.controller.endpoint.general;

import com.dah.desb.controller.endpoint.EndpointAction;
import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.endpoint.general.EndpointGeneral;
import com.dah.desb.domain.endpoint.general.EndpointGeneralMapper;
import com.dah.desb.domain.endpoint.general.EndpointGeneralService;
import com.dah.desb.domain.endpoint.http.EndpointHttp;
import com.dah.desb.domain.endpoint.http.EndpointHttpMapper;
import com.dah.desb.domain.endpoint.http.EndpointHttpService;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessType;
import com.dah.desb.infrastructure.mybatis.Pagination;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EndpointGeneralController {

	@Autowired
	private EndpointGeneralMapper endpointGeneralMapper;
	
	@Autowired
	private EndpointGeneralService endpointGeneralService;

	@Autowired
	private EndpointAction endpointAction;
	
	@GetMapping(path = "/generals", produces = "text/html")
	public String toPage(Model model) {
		model.addAttribute("process", GeneralProcessType.values());
		return "/view/endpoint/general/endpoint-generals";
	}

	@PostMapping(path = "/generals", produces = "application/json")
	@ResponseBody
	public EndpointGeneral save(@RequestBody EndpointGeneral endpointGeneral) {
		return endpointGeneralService.save(endpointGeneral);
	}
	
	@PutMapping(path = "/generals/{id}", produces = "application/json")
	@ResponseBody
	public EndpointGeneral update(@PathVariable("id") String id, @RequestBody EndpointGeneral endpointGeneral) {
		endpointGeneral.setId(id);
		EndpointGeneral result = endpointGeneralService.update(endpointGeneral);
		endpointAction.restartRouteByEndpoint(EndpointType.http, id);
		return result;
	}
	
	@DeleteMapping(path = "/generals/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		endpointGeneralService.remove(id);
	}
	
	@GetMapping(path = "/generals/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<EndpointGeneral> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
	    Page<EndpointGeneral> page = endpointGeneralMapper.getPage(search, new RowBounds(pageNum, pageSize));
	    return new Pagination<EndpointGeneral>(page);
	}
	
}
