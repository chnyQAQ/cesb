package com.dah.desb.controller.route;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.domain.route.log.RouteLog;
import com.dah.desb.domain.route.log.RouteLogMapper;
import com.dah.desb.domain.route.runtime.RouteManager;
import com.dah.desb.infrastructure.mybatis.Pagination;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RouteLogController {

    @Autowired
    private RouteLogMapper routeLogMapper;

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    @Autowired
    private EndpointMapperRegistry endpointMapperRegistry;

    @GetMapping(path = "/routes/{id}/logs", produces = "text/html")
    public String toPage(@PathVariable("id") String id, Model model) {
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
        return "/view/route/log/logs";
    }

    @GetMapping(path = "/routes/{id}/logs/list-page", produces = "application/json")
    @ResponseBody
    public Pagination<RouteLog> getPage(@PathVariable("id") String routeId,
                                        @RequestParam(value = "beginTime", required = false) Date beginTime,
                                        @RequestParam(value = "endTime", required = false) Date endTime,
                                        @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return new Pagination<>(routeLogMapper.getPage(routeId, beginTime, endTime, new RowBounds(pageNum, pageSize)));
    }
}
