package com.dah.desb.domain.route;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.domain.route.endpoint.RouteEndpointService;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogService;
import com.dah.desb.domain.route.log.RouteLogService;
import com.dah.desb.infrastructure.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
@Service
public class RouteService {

    @Autowired
    private RouteMapper routemapper;

    @Autowired
    private RouteLogService routeLogService;

    @Autowired
    RouteEndpointMapper routeEndpointMapper;

    @Autowired
    private RouteEndpointService routeEndpointService;

    @Autowired
    private RouteEndpointLogService routeEndpointLogService;

    public Route save(@Valid Route route) {

        Route saved = routemapper.getByCode(route.getCode());
        if (saved != null) {
            throw new AppException("保存失败，代码重复！");
        }
        saved = routemapper.getByName(route.getName());
        if (saved != null && !saved.getId().equals(route.getId())) {
            throw new AppException("保存失败，名称重复！");
        }
        route.setId(UUID.randomUUID().toString());

        routemapper.save(route);
        return route;
    }

    public Route update(@Valid Route route) {
        Route saved = routemapper.getById(route.getId());
        if (saved == null) {
            throw new AppException("更新失败，路由不存在！");
        }
        saved = routemapper.getByCode(route.getCode());
        if (saved != null && !saved.getId().equals(route.getId())) {
            throw new AppException("更新失败，代码重复！");
        }
        saved = routemapper.getByName(route.getName());
        if (saved != null && !saved.getId().equals(route.getId())) {
            throw new AppException("更新失败，名称重复！");
        }

        saved.setName(route.getName());
        saved.setCode(route.getCode());
        saved.setEnabled(route.isEnabled());

        routemapper.update(saved);
        return saved;
    }

    public void updateExecuteInfo(String id, Integer executeCount, Integer executeAvgDuration) {
        routemapper.updateExecuteInfo(id, executeCount, executeAvgDuration);
    }

    public Route design(@Valid Route route) {
        Route saved = routemapper.getById(route.getId());
        if (saved == null) {
            throw new AppException("更新失败，路由不存在！");
        }
        saved.setRouteXML(route.getRouteXML());
        List<RouteEndpoint> routeEndpoints = route.getRouteEndpoints();
        saved.setRouteEndpoints(routeEndpoints);

        if (routeEndpoints != null && routeEndpoints.size() > 0) {
            routeEndpointService.removeByRoute(route.getId());
            for (RouteEndpoint routeEndpoint : routeEndpoints) {
                routeEndpointService.save(routeEndpoint);
            }
        }

        routemapper.update(saved);
        return saved;
    }

    public void remove(String id) {
        Route saved = routemapper.getById(id);
        if (saved == null) {
            throw new AppException("删除失败，路由不存在！");
        }
        // 删除路由-删除路由端点
        routeEndpointService.removeByRoute(id);

        // 删除路由-删除路由执行日志
        routeLogService.removeByRoute(id);

        // 删除路由-删除相应路由端点执行日志
        routeEndpointLogService.removeByRoute(id);

        routemapper.remove(id);
    }

}
