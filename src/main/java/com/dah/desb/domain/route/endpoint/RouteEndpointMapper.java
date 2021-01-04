package com.dah.desb.domain.route.endpoint;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RouteEndpointMapper {
	
    RouteEndpoint getById(@Param("id") String id);

    RouteEndpoint getFrom(@Param("routeId") String routeId);

    List<RouteEndpoint> getListByRoute(@Param("routeId") String routeId);

    List<RouteEndpoint> getNextList(@Param("previousRouteEndpointId") String previousRouteEndpointId);

    List<RouteEndpoint> getListByEndpoint(@Param("endpointType") String endpointType, @Param("endpointId") String endpointId);

    int save(RouteEndpoint routeEndpoint);

    int update(RouteEndpoint routeEndpoint);

    void remove(@Param("id") String id);

    void removeByRoute(@Param("routeId") String routeId);
}
