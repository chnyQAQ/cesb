package com.dah.desb.controller.endpoint;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLog;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogMapper;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.infrastructure.exception.AppException;
import com.dah.desb.infrastructure.mybatis.Pagination;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class EndpointLogController {

    @Autowired
    private RouteEndpointLogMapper routeEndpointLogMapper;

    @Autowired
    private EndpointMapperRegistry endpointMapperRegistry;

    @Autowired
    private RouteMapper routeMapper;

    @GetMapping(path = "/endpoint/{id}/logs", produces = "text/html")
    public String toPageByEndpoint(@PathVariable("id") String endpointId, Model model, @RequestParam("endpointPage") String endpointPage,
                                   @RequestParam("endpointType") String endpointType) throws RouteException {
        model.addAttribute("endpointId", endpointId);
        if (StringUtils.isEmpty(endpointPage)) {
            throw new AppException("访问失败，请从端点页面进行访问！");
        }
        Endpoint endpoint = endpointMapperRegistry.determineEndpoint(endpointType, endpointId);
        model.addAttribute("endpoint", endpoint);
        model.addAttribute("endpointPage", endpointPage);
        return "/view/endpoint/log";
    }

    @GetMapping(path = "/endpoint/{id}/logs/list-page", produces = "application/json")
    @ResponseBody
    public Pagination<RouteEndpointLog> getPageByEndpoint(@PathVariable("id") String endpointId,
                                                          @RequestParam(value = "beginTime", required = false) Date beginTime,
                                                          @RequestParam(value = "endTime", required = false) Date endTime,
                                                          @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Pagination<RouteEndpointLog> pagination = new Pagination<>(routeEndpointLogMapper.getPageByEndpoint(endpointId, beginTime, endTime, new RowBounds(pageNum, pageSize)));
        for (RouteEndpointLog routeEndpointLog : pagination.getRows()) {
            Route route = routeMapper.getById(routeEndpointLog.getRouteId());
            routeEndpointLog.setRouteName(route != null ? route.getName() : "");
        }
        return pagination;
    }
}
