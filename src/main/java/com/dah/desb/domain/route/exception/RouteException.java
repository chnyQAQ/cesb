package com.dah.desb.domain.route.exception;

public class RouteException extends Exception {

	private static final long serialVersionUID = 1L;

	public RouteException() {
	}

	public RouteException(String message) {
		super(message);
	}

	public RouteException(String message, Throwable cause) {
		super(message, cause);
	}

}
