package com.dah.desb.domain.route.endpoint;

import org.hibernate.validator.constraints.Length;

public class RouteEndpoint {

    @Length(max = 50)
    private String id;

    /**
     * 所属 路由定义 记录ID
     */
    @Length(max = 50)
    private String routeId;


    /**
     * endpointType 指向某张表
     */
    @Length(max = 20)
    private String endpointType;

    /**
     * endpointId 指向endpoint_{endpointType}表中的某条数据
     */
    @Length(max = 50)
    private String endpointId;

    /**
     * 表达式（动态路由的条件）
     */
    @Length(max = 500)
    private String expression;

    /**
     * 当前控制端点的上一个端点的id（为空则表示为路由的from）
     */
    @Length(max = 50)
    private String previousId;

    //transient
    //当前端点的 Uri
    private String endpointUrl;
    private String endpointName;
    private String endpointCode;

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

    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getPreviousId() {
        return previousId;
    }

    public void setPreviousId(String previousId) {
        this.previousId = previousId;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
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
}
