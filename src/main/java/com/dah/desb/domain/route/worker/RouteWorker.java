package com.dah.desb.domain.route.worker;

import com.dah.desb.domain.route.RouteService;
import com.dah.desb.domain.route.log.RouteLog;
import com.dah.desb.domain.route.log.RouteLogMapper;
import com.dah.desb.infrastructure.worker.cron.AbstractSchelduledTaskWorker;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteWorker extends AbstractSchelduledTaskWorker {

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteLogMapper routeLogMapper;

    public RouteWorker() {
    }

    @Override
    protected void execute(Map<Object, Object> context) {
        List<RouteLog> routeLogs = routeLogMapper.getAll();
        if (routeLogs != null && routeLogs.size() > 0) {
            Map<String, Integer> countMap = new HashMap<>();
            Map<String, Long> durationMap = new HashMap<>();
            for (RouteLog routeLog : routeLogs) {
                // 执行次数
                if (countMap.containsKey(routeLog.getRouteId())) {
                    countMap.put(routeLog.getRouteId(), countMap.get(routeLog.getRouteId()) + 1);
                } else {
                    countMap.put(routeLog.getRouteId(), 1);
                }

                // 耗时
                if (routeLog.getEndTime() != null) {
                    long duration = routeLog.getEndTime().getTime() - routeLog.getBeginTime().getTime();
                    if (durationMap.containsKey(routeLog.getRouteId())) {
                        durationMap.put(routeLog.getRouteId(), durationMap.get(routeLog.getRouteId()) + duration);
                    } else {
                        durationMap.put(routeLog.getRouteId(), duration);
                    }
                }
            }
            for (String routeId : countMap.keySet()) {
                int count = countMap.get(routeId);
                int avg = 0;
                Long duration = durationMap.get(routeId);
                if (duration != null) {
                    avg = duration.intValue() / count;
                }
                routeService.updateExecuteInfo(routeId, count, avg);
            }
        }
    }
}
