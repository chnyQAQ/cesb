package com.dah.desb.controller.statistics;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLog;
import com.dah.desb.domain.route.endpoint.log.RouteEndpointLogMapper;
import com.dah.desb.domain.route.exception.RouteException;
import com.dah.desb.infrastructure.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class StatisticsEndPointController {

    private final static Logger logger = LoggerFactory.getLogger(StatisticsEndPointController.class);

    @Autowired
    private EndpointMapperRegistry endpointMapperRegistry;

    @Autowired
    private RouteEndpointLogMapper routeEndpointLogMapper;

    @GetMapping(path = "/statistics-endpoint", produces = "text/html")
    public String toPage() {
        return "/view/statistics/endpoint/endpoint";
    }

    @GetMapping(path = "/statistics-endpoint/count", produces = "application/json")
    @ResponseBody
    public int getRouteCount() {
        int count = 0;
        for (EndpointType type : EndpointType.values()) {
            try {
                count += endpointMapperRegistry.getCount(type.toString());
            } catch (RouteException e) {
                logger.error("端点统计分析", e.getMessage());
            }
        }
        return count;
    }

    // 执行情况
    @GetMapping(path = "/statistics-endpoint/execute-info", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getRouteExecuteInfo() throws RouteException {
        Map<String, Object> result = new HashMap<>();
        result.put("executeSuccessCount", routeEndpointLogMapper.getCount(false));
        result.put("executeFailCount", routeEndpointLogMapper.getCount(true));
        return result;
    }

    // 执行情况top10
    @GetMapping(path = "/statistics-endpoint/execute-info-top10", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getRouteExecuteInfoTop10() throws RouteException {
        Map<String, Object> result = new HashMap<>();

        // 执行次数top10
        List<RouteEndpointLog> routes1 = routeEndpointLogMapper.getExecuteCountTop10();
        result.put("executeTop10", buildCountTopMap(routes1));

        // 执行耗时top10
        List<RouteEndpointLog> routes2 = routeEndpointLogMapper.getExecuteDurationTop10();
        result.put("executeTimeTop10", buildDurationTopMap(routes2));
        return result;
    }

    private Map<String, Object> buildCountTopMap(List<RouteEndpointLog> routeEndpointLogs) throws RouteException {
        if (routeEndpointLogs != null && routeEndpointLogs.size() > 0) {
            Map<String, Object> map = new HashMap<>();
            List<String> xAxis = new ArrayList<>();
            List<Integer> series = new ArrayList<>();
            for (RouteEndpointLog routeEndpointLog : routeEndpointLogs) {
                Endpoint endpoint = endpointMapperRegistry.determineEndpoint(routeEndpointLog.getEndpointType(), routeEndpointLog.getEndpointId());
                if (endpoint != null) {
                    xAxis.add(endpoint.getName() + "(" + endpoint.getType() + ")");
                    series.add(routeEndpointLog.getCount());
                }
            }
            map.put("xAxis", xAxis);
            map.put("series", series);
            return map;
        }
        return null;
    }

    private Map<String, Object> buildDurationTopMap(List<RouteEndpointLog> routeEndpointLogs) throws RouteException {
        if (routeEndpointLogs != null && routeEndpointLogs.size() > 0) {
            Map<String, Object> map = new HashMap<>();
            List<String> xAxis = new ArrayList<>();
            List<Long> series = new ArrayList<>();
            for (RouteEndpointLog routeEndpointLog : routeEndpointLogs) {
                Endpoint endpoint = endpointMapperRegistry.determineEndpoint(routeEndpointLog.getEndpointType(), routeEndpointLog.getEndpointId());
                if (endpoint != null) {
                    xAxis.add(endpoint.getName() + "(" + endpoint.getType() + ")");
                    series.add(routeEndpointLog.getDuration());
                }
            }
            map.put("xAxis", xAxis);
            map.put("series", series);
            return map;
        }
        return null;
    }

    // 服务质量
    @GetMapping(path = "/statistics-endpoint/service-duration", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getRouteServiceDuration(@RequestParam("dayBegin") Date dayBegin, @RequestParam("dayEnd") Date dayEnd) {

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(dayBegin);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.SECOND, 0);
        beginCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(dayEnd);
        endCalendar.add(Calendar.DAY_OF_MONTH, 1);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        List<RouteEndpointLog> routeEndpointLogs = routeEndpointLogMapper.getList(beginCalendar.getTime(), endCalendar.getTime());
        return buildRouteServiceInfoMap(beginCalendar, endCalendar, routeEndpointLogs);
    }

    // 构建服务质量map
    private Map<String, Object> buildRouteServiceInfoMap(Calendar beginCalendar, Calendar endCalendar, List<RouteEndpointLog> routeEndpointLogs) {
        Map<String, Object> map = new HashMap<>();
        if (routeEndpointLogs != null && routeEndpointLogs.size() > 0) {
            // 调用频次
            Map<String, Integer> callCountMap = new HashMap<>();
            // 调用成功次数
            Map<String, Integer> successCountMap = new HashMap<>();

            for (RouteEndpointLog routeEndpointLog : routeEndpointLogs) {
                String day = DateUtils.getDate(routeEndpointLog.getBeginTime());
                if (callCountMap.containsKey(day)) {
                    callCountMap.put(day, callCountMap.get(day) + 1);
                } else {
                    callCountMap.put(day, 1);
                }
                if (!routeEndpointLog.isHasException()) {
                    if (successCountMap.containsKey(day)) {
                        successCountMap.put(day, successCountMap.get(day) + 1);
                    } else {
                        successCountMap.put(day, 1);
                    }
                }
            }

            // 日期间隔天数
            int days = (int) ((endCalendar.getTime().getTime() - beginCalendar.getTime().getTime()) / (1000 * 3600 * 24));
            List<String> xAxis = new ArrayList<>();
            // 调用频次
            List<Integer> callCounts = new ArrayList<>();
            // 调用成功次数
            List<Integer> successCounts = new ArrayList<>();

            for (int i = 0; i < days; i++) {
                if (i != 0) {
                    beginCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                String day = DateUtils.getDate(beginCalendar.getTime());
                xAxis.add(day);
                callCounts.add(callCountMap.get(day) == null ? 0 : callCountMap.get(day));
                successCounts.add(successCountMap.get(day) == null ? 0 : successCountMap.get(day));
            }
            map.put("xAxis", xAxis);
            map.put("callCounts", callCounts);
            map.put("successCounts", successCounts);
            return map;
        }
        return null;
    }

}
