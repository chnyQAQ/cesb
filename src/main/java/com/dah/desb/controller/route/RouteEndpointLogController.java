package com.dah.desb.controller.route;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLog;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogMapper;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.domain.route.log.RouteLog;
import com.dah.desb.domain.route.log.RouteLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RouteEndpointLogController {

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    @Autowired
    private RouteLogMapper routeLogMapper;

    @Autowired
    private RouteEndpointLogMapper routeEndpointLogMapper;

    @Autowired
    private EndpointMapperRegistry endpointMapperRegistry;

    @GetMapping(path = "/routes/logs/{id}", produces = "text/html")
    public String toPageByRoute(@PathVariable("id") String id, Model model) {
        RouteLog routeLog = routeLogMapper.getById(id);
        model.addAttribute("routeLog", routeLog);
        if (routeLog != null) {
            model.addAttribute("route", routeMapper.getById(routeLog.getRouteId()));
            Map<String, Endpoint> endpointMap = new HashMap<>();
            try {
                for (RouteEndpoint routeEndpoint : routeEndpointMapper.getListByRoute(routeLog.getRouteId())) {
                    endpointMap.put(routeEndpoint.getEndpointId(), endpointMapperRegistry.determineEndpoint(routeEndpoint));
                }
            } catch (RouteException e) {
                e.printStackTrace();
            }
            model.addAttribute("endpointMap", endpointMap);
        }
        return "/view/route/log/log";
    }

    @GetMapping(path = "/routes/logs/{id}/list-all", produces = "application/json")
    @ResponseBody
    public List<RouteEndpointLog> getListByRouteLog(@PathVariable("id") String routeLogId) {
        List<RouteEndpointLog> routeEndpointLogs = routeEndpointLogMapper.getListByRouteLog(routeLogId);
        extend(routeEndpointLogs);
        return routeEndpointLogs;
    }

    public void extend(List<RouteEndpointLog> routeEndpointLogs) {
        try {
            for (RouteEndpointLog routeEndpointLog : routeEndpointLogs) {
                Endpoint endpoint = endpointMapperRegistry.determineEndpoint(routeEndpointLog.getEndpointType(), routeEndpointLog.getEndpointId());
                if (endpoint != null) {
                    routeEndpointLog.setEndpointCode(endpoint.getCode());
                    routeEndpointLog.setEndpointName(endpoint.getName());
                }
            }
        } catch (RouteException e) {
            e.printStackTrace();
        }
    }
}
