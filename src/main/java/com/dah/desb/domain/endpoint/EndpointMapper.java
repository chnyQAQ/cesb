package com.dah.desb.domain.endpoint;

public interface EndpointMapper {

	int getCount();

	Endpoint getById(String id);

	Object getSearch(String search);

}
