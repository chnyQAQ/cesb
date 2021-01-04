package com.dah.desb.domain.route.endpoint;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.infrastructure.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Validated
@Service
public class RouteEndpointService {

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    @Autowired
    private EndpointMapperRegistry endpointMapperRegistry;

    public RouteEndpoint save(@Valid RouteEndpoint routeEndpoint) {
        if (StringUtils.isEmpty(routeEndpoint.getId())) {
            routeEndpoint.setId(UUID.randomUUID().toString());
        }
        try {
            Endpoint endpoint = endpointMapperRegistry.determineEndpoint(routeEndpoint);
            if (StringUtils.isEmpty(endpoint.buildUrl())) {
                throw new AppException("操作失败，路由端点 " + routeEndpoint.getEndpointName() + " 地址为空！");
            }
            if (StringUtils.isEmpty(routeEndpoint.getPreviousId()) && endpoint.buildUrl().startsWith("mock")) {
                throw new AppException("操作失败，路由不能由Mock作为起始端点！" + routeEndpoint.getEndpointName());
            }
        } catch (RouteException e) {
            throw new AppException("操作失败，获取路由端点原始信息失败！" + routeEndpoint.getEndpointName());
        }

        routeEndpointMapper.save(routeEndpoint);
        return routeEndpoint;
    }

    public RouteEndpoint update(String id, @Valid RouteEndpoint routeEndpoint) {
        RouteEndpoint saved = routeEndpointMapper.getById(id);
        if (routeEndpoint == null) {
            throw new AppException("操作失败，路由端点不存在！");
        }
        routeEndpointMapper.update(routeEndpoint);
        return routeEndpoint;
    }

    public void remove(String id) {
        RouteEndpoint routeEndpoint = routeEndpointMapper.getById(id);
        if (routeEndpoint == null) {
            throw new AppException("操作失败，路由端点不存在！");
        }
        routeEndpointMapper.remove(id);
    }

    public void removeByRoute(String routeId) {
        routeEndpointMapper.removeByRoute(routeId);
    }
}
