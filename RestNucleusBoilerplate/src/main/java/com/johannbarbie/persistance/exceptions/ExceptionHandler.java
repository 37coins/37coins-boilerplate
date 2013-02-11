package com.johannbarbie.persistance.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements
		ExceptionMapper<WebApplicationException> {

	public Response toResponse(WebApplicationException exception) {
		return exception.getResponse();
	}

}