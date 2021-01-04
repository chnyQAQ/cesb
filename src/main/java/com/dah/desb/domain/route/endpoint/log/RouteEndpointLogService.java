package com.dah.desb.domain.route.endpoint.log;

import com.dah.desb.infrastructure.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Validated
@Service
public class RouteEndpointLogService {

    @Autowired
    private RouteEndpointLogMapper routeEndpointLogMapper;

    public RouteEndpointLog save(@Valid RouteEndpointLog routeEndpointLog) {
        routeEndpointLog.setId(UUID.randomUUID().toString());
        routeEndpointLogMapper.save(routeEndpointLog);
        return routeEndpointLog;
    }

    public void remove(String id) {
        RouteEndpointLog routeEndpointLog = routeEndpointLogMapper.getById(id);
        if (routeEndpointLog == null) {
            throw new AppException("操作失败，当前路由端点日志信息不存在！");
        }
        routeEndpointLogMapper.remove(id);
    }

    public void removeByRoute(String routeId) {
        routeEndpointLogMapper.removeByRoute(routeId);
    }

}
