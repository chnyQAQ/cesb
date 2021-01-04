package com.dah.desb.domain.route.endpoint.log;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;

@Mapper
public interface RouteEndpointLogMapper {

    int getCount(@Param("hasException") boolean hasException);

    RouteEndpointLog getById(@Param("id") String id);

    List<RouteEndpointLog> getByRoute(@Param("routeId") String routeId);

    List<RouteEndpointLog> getByRouteLog(@Param("routeLogId") String routeLogId);

    List<RouteEndpointLog> getListByRouteLog(@Param("routeLogId") String routeLogId);

    List<RouteEndpointLog> getExecuteCountTop10();

    List<RouteEndpointLog> getExecuteDurationTop10();

    List<RouteEndpointLog> getList(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    Page<RouteEndpointLog> getPageByEndpoint(@Param("endpointId") String endpointId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, RowBounds rowBounds);

    int save(RouteEndpointLog routeEndpointLog);

    int remove(@Param("id") String id);

    int removeByRoute(@Param("routeId") String routeId);
}
