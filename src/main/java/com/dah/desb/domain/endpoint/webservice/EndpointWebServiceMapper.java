package com.dah.desb.domain.endpoint.webservice;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.dah.desb.domain.endpoint.EndpointMapper;
import com.dah.desb.domain.endpoint.EndpointTypeSupport;
import com.dah.desb.domain.endpoint.EndpointType;
import com.github.pagehelper.Page;

import java.util.List;

@Mapper
@EndpointTypeSupport(EndpointType.webservice)
public interface EndpointWebServiceMapper extends EndpointMapper {

	int getCount();

	EndpointWebService getById(@Param("id") String id);

	EndpointWebService getByCode(@Param("code") String code);

	EndpointWebService getByName(@Param("name") String name);

	Page<EndpointWebService> getPage(@Param("search") String search, RowBounds rowBounds);

	List<EndpointWebService> getSearch(@Param("search") String search);

	void save(EndpointWebService endpointWebService);

	void update(EndpointWebService endpointWebService);

	void remove(@Param("id") String id);

}