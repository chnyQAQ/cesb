package com.dah.desb.domain.route.log;

import com.dah.desb.infrastructure.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Validated
@Service
public class RouteLogService {

    @Autowired
    private RouteLogMapper routeLogMapper;

    public RouteLog save(@Valid RouteLog routeLog) {
        if (StringUtils.isEmpty(routeLog.getId())) {
            routeLog.setId(UUID.randomUUID().toString());
        }
        if (routeLogMapper.getById(routeLog.getId()) != null) {
            throw new AppException("操作失败，当前记录已存在！");
        }
        routeLogMapper.save(routeLog);
        return routeLog;
    }

    public RouteLog update(@Valid RouteLog routeLog) {
        RouteLog saved = routeLogMapper.getById(routeLog.getId());
        if (saved == null) {
            throw new AppException("操作失败，当前路由日志信息不存在！");
        }
        saved.setRouteId(routeLog.getRouteId());
        saved.setInvoke(routeLog.getInvoke());
        saved.setSuccess(routeLog.isSuccess());
        saved.setBeginTime(routeLog.getBeginTime());
        saved.setEndTime(routeLog.getEndTime());
        routeLogMapper.update(saved);
        return routeLog;
    }

    public void remove(String id) {
        RouteLog saved = routeLogMapper.getById(id);
        if (saved == null) {
            throw new AppException("操作失败，当前路由日志信息不存在！");
        }
        routeLogMapper.remove(id);
    }

    public void removeByRoute(String routeId) {
        routeLogMapper.removeByRoute(routeId);
    }

}
