package com.dah.desb.domain.route.log;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;

@Mapper
public interface RouteLogMapper {

    RouteLog getById(@Param("id") String id);

    List<RouteLog> getByRoute(@Param("routeId") String routeId);

    Page<RouteLog> getPage(@Param("routeId") String routeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, RowBounds rowBounds);

    List<RouteLog> getByBeginTime(@Param("beginTime") Date beginTime);

    List<RouteLog> getList(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    List<RouteLog> getAll();

    int save(RouteLog routeLog);

    int update(RouteLog routeLog);

    int remove(@Param("id") String id);

    int removeByRoute(@Param("routeId") String routeId);

}
