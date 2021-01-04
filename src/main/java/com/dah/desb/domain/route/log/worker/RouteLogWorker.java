package com.dah.desb.domain.route.log.worker;

import com.dah.desb.domain.route.endpoint.log.RouteEndpointLog;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogMapper;
import com.dah.desb.domain.route.log.RouteLog;
import com.dah.desb.domain.route.log.RouteLogMapper;
import com.dah.desb.domain.route.log.RouteLogService;
import com.dah.desb.infrastructure.worker.cron.AbstractSchelduledTaskWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RouteLogWorker extends AbstractSchelduledTaskWorker {

    private static final Logger logger = LoggerFactory.getLogger(RouteLogWorker.class);

    @Autowired
    private RouteLogMapper routeLogMapper;

    @Autowired
    private RouteLogService routeLogService;

    @Autowired
    private RouteEndpointLogMapper routeEndpointLogMapper;

    private long minutes;

    public RouteLogWorker(long minutes) {
        this.minutes = minutes;
    }

    @Override
    protected void execute(Map<Object, Object> context) {
        updateRouteLog();
    }

    public synchronized void updateRouteLog() {
        List<RouteLog> routeLogs = new ArrayList<>();
        if (minutes == 0) {
            routeLogs = routeLogMapper.getAll();
        } else {
            routeLogs = routeLogMapper.getByBeginTime((new Date(new Date().getTime() - minutes  * 60 * 1000L)));
        }
        for (RouteLog routeLog : routeLogs) {
            List<RouteEndpointLog> routeEndpointLogs = routeEndpointLogMapper.getByRouteLog(routeLog.getId());
            boolean success = true;
            Date endTime = null;
            for (RouteEndpointLog routeEndpointLog : routeEndpointLogs) {
                if (endTime == null) {
                    endTime = routeEndpointLog.getEndTime();
                } else {
                    if (endTime.getTime() < routeEndpointLog.getEndTime().getTime()) {
                        endTime = routeEndpointLog.getEndTime();
                    }
                }
                if (routeEndpointLog.isHasException()) {
                    success =false;
                }
            }
            if (endTime.getTime() < routeLog.getBeginTime().getTime()) {
                logger.error("更新路由日志失败，路由日志结束时间小于开始时间 -> " + routeLog.getRouteId());
                break;
            }
            routeLog.setEndTime(endTime);
            routeLog.setSuccess(success);
            routeLogService.update(routeLog);
        }
    }

}
