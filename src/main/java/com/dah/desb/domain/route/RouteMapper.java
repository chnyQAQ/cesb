package com.dah.desb.domain.route;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface RouteMapper {

    int getCount();

	Route getById(@Param("id") String id);

	Route getByCode(@Param("code") String code);

	Route getByName(@Param("name") String name);

	List<Route> getAll();

    List<Route> getExecuteCountTop10();

    List<Route> getExecuteDurationTop10();

	Page<Route> getPage(@Param("search") String search,
						RowBounds rowBounds);

	void save(Route route);
	
	void update(Route route);

    void updateExecuteInfo(@Param("id") String id, @Param("executeCount") Integer executeCount, @Param("executeAvgDuration") Integer executeAvgDuration);

	void remove(@Param("id") String id);

}
