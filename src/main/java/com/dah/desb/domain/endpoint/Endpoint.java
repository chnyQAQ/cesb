package com.dah.desb.domain.endpoint;

public interface Endpoint {

	String getType();

	String getCode();

	String getName();
	
	String buildUrl();
	
}
