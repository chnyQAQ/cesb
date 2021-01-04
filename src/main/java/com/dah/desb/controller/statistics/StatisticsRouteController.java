package com.dah.desb.controller.statistics;

import com.dah.desb.domain.route.Route;
import com.dah.desb.domain.route.RouteMapper;
import com.dah.desb.domain.route.log.RouteLog;
import com.dah.desb.domain.route.log.RouteLogMapper;
import com.dah.desb.infrastructure.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatisticsRouteController {

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private RouteLogMapper routeLogMapper;

    @GetMapping(path = "/statistics-route", produces = "text/html")
    public String toPage() {
        return "/view/statistics/route/route";
    }

    @GetMapping(path = "/statistics-route/count", produces = "application/json")
    @ResponseBody
    public int getRouteCount() {
        return routeMapper.getCount();
    }

    @GetMapping(path = "/statistics-route/service-duration", produces = "application/json")
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

        List<RouteLog> routeLogs = routeLogMapper.getList(beginCalendar.getTime(), endCalendar.getTime());
        return buildRouteServiceInfoMap(beginCalendar, endCalendar, routeLogs);
    }

    // 构建服务质量map
    private Map<String, Object> buildRouteServiceInfoMap(Calendar beginCalendar, Calendar endCalendar, List<RouteLog> routeLogs) {
        Map<String, Object> map = new HashMap<>();
        if (routeLogs != null && routeLogs.size() > 0) {
            // 调用频次
            Map<String, Integer> callCountMap = new HashMap<>();
            // 调用成功次数
            Map<String, Integer> successCountMap = new HashMap<>();

            for (RouteLog routeLog : routeLogs) {
                String day = DateUtils.getDate(routeLog.getBeginTime());
                if (callCountMap.containsKey(day)) {
                    callCountMap.put(day, callCountMap.get(day) + 1);
                } else {
                    callCountMap.put(day, 1);
                }
                if (routeLog.isSuccess()) {
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

    @GetMapping(path = "/statistics-route/execute-info", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getRouteExecuteInfo() {
        Map<String, Object> result = new HashMap<>();
        // 初始化
        result.put("executeSuccessCount", 0);
        result.put("executeFailCount", 0);
        result.put("executeTop10", null);
        result.put("executeTimeTop10", null);

        List<RouteLog> routeLogs = routeLogMapper.getAll();
        if (routeLogs != null && routeLogs.size() > 0) {
            int executeSuccessCount = 0;
            Map<String, Integer> executeTop10Map = new HashMap<>();
            Map<String, Integer> executeTimeTop10Map = new HashMap<>();
            for (RouteLog routeLog : routeLogs) {
                // 执行成功
                if (routeLog.isSuccess()) {
                    executeSuccessCount++;
                }
            }
            // 执行次数top10
            List<Route> routes1 = routeMapper.getExecuteCountTop10();
            // 执行平均耗时top10
            List<Route> routes2 = routeMapper.getExecuteDurationTop10();

            result.put("executeSuccessCount", executeSuccessCount);
            result.put("executeFailCount", routeLogs.size() - executeSuccessCount);
            result.put("executeTop10", buildCountTopMap(routes1));
            result.put("executeTimeTop10", buildDurationTopMap(routes2));
        }
        return result;
    }

    private Map<String, Object> buildCountTopMap(List<Route> routes) {
        if (routes != null && routes.size() > 0) {
            Map<String, Object> map = new HashMap<>();
            List<String> xAxis = routes.stream().map(route -> route.getName()).collect(Collectors.toList());
            List<Integer> yAxis = routes.stream().map(route -> route.getExecuteCount()).collect(Collectors.toList());
            map.put("xAxis", xAxis);
            map.put("series", yAxis);
            return map;
        }
        return null;
    }

    private Map<String, Object> buildDurationTopMap(List<Route> routes) {
        if (routes != null && routes.size() > 0) {
            Map<String, Object> map = new HashMap<>();
            List<String> xAxis = routes.stream().map(route -> route.getName()).collect(Collectors.toList());
            List<Integer> yAxis = routes.stream().map(route -> route.getExecuteAvgDuration()).collect(Collectors.toList());
            map.put("xAxis", xAxis);
            map.put("series", yAxis);
            return map;
        }
        return null;
    }
}
