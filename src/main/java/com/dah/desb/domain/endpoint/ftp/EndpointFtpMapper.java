package com.dah.desb.domain.endpoint.ftp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.dah.desb.domain.endpoint.EndpointMapper;
import com.dah.desb.domain.endpoint.EndpointTypeSupport;
import com.dah.desb.domain.endpoint.EndpointType;
import com.github.pagehelper.Page;

import java.util.List;

@Mapper
@EndpointTypeSupport(EndpointType.ftp)
public interface EndpointFtpMapper extends EndpointMapper {

	int getCount();

	EndpointFtp getById(@Param("id") String id);

	EndpointFtp getByCode(@Param("code") String code);

	EndpointFtp getByName(@Param("name") String name);

	Page<EndpointFtp> getPage(@Param("search") String search, RowBounds rowBounds);

	List<EndpointFtp> getSearch(@Param("search") String search);

	void save(EndpointFtp endpointFtp);

	void update(EndpointFtp endpointFtp);

	void remove(@Param("id") String id);

}