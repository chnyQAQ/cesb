package com.dah.desb.domain.endpoint.rest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.dah.desb.domain.endpoint.EndpointMapper;
import com.dah.desb.domain.endpoint.EndpointTypeSupport;
import com.dah.desb.domain.endpoint.EndpointType;
import com.github.pagehelper.Page;

import java.util.List;

@Mapper
@EndpointTypeSupport(EndpointType.rest)
public interface EndpointRestMapper extends EndpointMapper {

	int getCount();

	EndpointRest getById(@Param("id") String id);

	EndpointRest getByCode(@Param("code") String code);

	EndpointRest getByName(@Param("name") String name);

	Page<EndpointRest> getPage(@Param("search") String search, RowBounds rowBounds);

	List<EndpointRest> getSearch(@Param("search") String search);

	void save(EndpointRest endpointrest);

	void update(EndpointRest endpointrest);

	void remove(@Param("id") String id);

}
