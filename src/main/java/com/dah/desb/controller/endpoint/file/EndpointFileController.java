package com.dah.desb.controller.endpoint.file;

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

import com.dah.desb.controller.endpoint.EndpointAction;
import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.endpoint.file.EndpointFile;
import com.dah.desb.domain.endpoint.file.EndpointFileMapper;
import com.dah.desb.domain.endpoint.file.EndpointFileService;
import com.dah.desb.infrastructure.mybatis.Pagination;
import com.github.pagehelper.Page;

@Controller
public class EndpointFileController {

	@Autowired
	private EndpointFileService endpointFileService;

	@Autowired
	private EndpointFileMapper endpointfileMapper;

	@Autowired
	private EndpointAction endpointAction;

	@GetMapping(path = "/files", produces = "text/html")
	public String toPage() {
		return "/view/endpoint/file/endpoint-files";
	}

	@PostMapping(path = "/files", produces = "application/json")
	@ResponseBody
	public EndpointFile save(@RequestBody EndpointFile endpointfile) {
		return endpointFileService.save(endpointfile);
	}

	@PutMapping(path = "/files/{id}", produces = "application/json")
	@ResponseBody
	public EndpointFile update(@PathVariable("id") String id, @RequestBody EndpointFile endpointfile) {
		// 更新
		endpointfile.setId(id);
		EndpointFile result = endpointFileService.update(endpointfile);
		// 重启相关路由
		endpointAction.restartRouteByEndpoint(EndpointType.file, id);
		return result;
	}

	@DeleteMapping(path = "/files/{id}", produces = "application/json")
	@ResponseBody
	public void remove(@PathVariable("id") String id) {
		endpointFileService.remove(id);
	}

	@GetMapping(path = "/files/list-page", produces = "application/json")
	@ResponseBody
	public Pagination<EndpointFile> getPage(@RequestParam(value = "search", required = false) String search, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
		Page<EndpointFile> page = endpointfileMapper.getPage(search, new RowBounds(pageNum, pageSize));
		return new Pagination<EndpointFile>(page);
	}

}
