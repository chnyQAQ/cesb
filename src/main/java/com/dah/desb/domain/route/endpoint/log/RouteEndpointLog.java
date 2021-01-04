package com.dah.desb.domain.route.endpoint.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

public class RouteEndpointLog {

    @Length(max = 50)
    private String id;

    /**
     * 绑定路由（调用routeEndpoint）
     */
    @Length(max = 50)
    private String routeId;

    /**
     * 绑定路由日志（调用routeEndpoint）
     */
    @Length(max = 50)
    private String routeLogId;

    /**
     * 绑定端点
     */
    @Length(max = 50)
    private String endpointId;

    /**
     * 绑定端点的类型
     */
    @Length(max = 50)
    private String endpointType;
    
    /**
     * 调用开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date beginTime;

    /**
     * 调用结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date endTime;

    /**
     * 耗时
     */
    private long duration;

    /**
     * 调用是否成功（是否出现异常）
     */
    private boolean hasException = false;
    

    private String exceptionType = "";

    private String exceptionMessage = "";

    //translate
    private String endpointName;
    private String endpointCode;
    private String routeName;
    // 端点调用次数
    private int count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteLogId() {
        return routeLogId;
    }

    public void setRouteLogId(String routeLogId) {
        this.routeLogId = routeLogId;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public boolean isHasException() {
        return hasException;
    }

    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointCode() {
        return endpointCode;
    }

    public void setEndpointCode(String endpointCode) {
        this.endpointCode = endpointCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
