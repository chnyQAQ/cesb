package com.dah.desb.domain.endpoint.general;

import com.dah.desb.domain.endpoint.EndpointMapper;
import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.endpoint.EndpointTypeSupport;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
@EndpointTypeSupport(EndpointType.general)
public interface EndpointGeneralMapper extends EndpointMapper {

	int getCount();

	EndpointGeneral getById(@Param("id") String id);

	EndpointGeneral getByCode(@Param("code") String code);

	EndpointGeneral getByName(@Param("name") String name);

	Page<EndpointGeneral> getPage(@Param("search") String search, RowBounds rowBounds);

	List<EndpointGeneral> getSearch(@Param("search") String search);

	void save(EndpointGeneral general);

	void update(EndpointGeneral general);

	void remove(@Param("id") String id);

}
