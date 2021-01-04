package com.dah.desb.domain.endpoint.http;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.dah.desb.domain.endpoint.EndpointMapper;
import com.dah.desb.domain.endpoint.EndpointTypeSupport;
import com.dah.desb.domain.endpoint.EndpointType;
import com.github.pagehelper.Page;

import java.util.List;

@Mapper
@EndpointTypeSupport(EndpointType.http)
public interface EndpointHttpMapper extends EndpointMapper {

	int getCount();

	EndpointHttp getById(@Param("id") String id);

	EndpointHttp getByCode(@Param("code") String code);

	EndpointHttp getByName(@Param("name") String name);

	Page<EndpointHttp> getPage(@Param("search") String search, RowBounds rowBounds);

	List<EndpointHttp> getSearch(@Param("search") String search);

	void save(EndpointHttp endpointHttp);

	void update(EndpointHttp endpointHttp);

	void remove(@Param("id") String id);

}