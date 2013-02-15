package com.johannbarbie.persistance.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author johba
 */
public class PersistanceException extends WebApplicationException{
	
	private static final long serialVersionUID = 573200741821829683L;

	public PersistanceException(String message) {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
