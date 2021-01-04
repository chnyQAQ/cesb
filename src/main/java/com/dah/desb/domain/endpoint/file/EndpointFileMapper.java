package com.dah.desb.domain.endpoint.file;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.dah.desb.domain.endpoint.EndpointMapper;
import com.dah.desb.domain.endpoint.EndpointTypeSupport;
import com.dah.desb.domain.endpoint.EndpointType;
import com.github.pagehelper.Page;

import java.util.List;

@Mapper
@EndpointTypeSupport(EndpointType.file)
public interface EndpointFileMapper extends EndpointMapper {

	int getCount();

	EndpointFile getById(@Param("id") String id);

	EndpointFile getByCode(@Param("code") String code);

	EndpointFile getByName(@Param("name") String name);

	Page<EndpointFile> getPage(@Param("search") String search, RowBounds rowBounds);

	List<EndpointFile> getSearch(@Param("search") String search);

	void save(EndpointFile endpointfile);

	void update(EndpointFile endpointfile);

	void remove(@Param("id") String id);

}
