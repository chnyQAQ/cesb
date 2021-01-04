package com.dah.desb.domain.route.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

public class RouteLog {

    @Length(max = 50)
    private String id;

    /**
     * 绑定路由
     */
    @Length(max = 50)
    private String routeId;

    /**
     * 调用者信息
     */
    @Length(max = 500)
    private String invoke;

    /**
     * 调用是否成功（是否出现异常）
     */
    private boolean success;

    /**
     * 调用整个路由的开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S", timezone="GMT+8")
    private Date beginTime;

    /**
     * 调用整个路由的结束时间（worker 更新）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S", timezone="GMT+8")
    private Date endTime;

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

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
}
