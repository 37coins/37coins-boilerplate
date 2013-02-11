package com.johannbarbie.persistance.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class ParameterMissingException extends WebApplicationException {

	private static final long serialVersionUID = -6971225533326804439L;

	public ParameterMissingException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
            .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
