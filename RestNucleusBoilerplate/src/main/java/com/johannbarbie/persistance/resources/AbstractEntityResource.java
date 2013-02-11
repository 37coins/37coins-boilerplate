package com.johannbarbie.persistance.resources;

import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.EntityNotFoundException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;

@Produces(MediaType.APPLICATION_JSON)    
public abstract class AbstractEntityResource<E extends Model> extends
		AbstractResource {
	public static final String ENTITY_QUERY = "/{id}";
	
	protected abstract Class<E> getEntityClass();
	
	@PUT
	@Path(ENTITY_QUERY)
	public Long updateEntity(InputStream requestBodyStream,@PathParam("id") String id) {
		ObjectMapper om = new ObjectMapper();
		E a;
		try {
			a = om.readValue(requestBodyStream, getEntityClass());
		} catch (Exception e1) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		try {
			a.setId(Long.parseLong(id));
			dao.update(a,getEntityClass());
		} catch (ParameterMissingException e) {
		}
		return a.getId();
	}

	@GET
	@Path(ENTITY_QUERY)
	public E getEntity(@PathParam("id") String id) {
		Long i = Long.parseLong(id);
		E e = dao.findById(i,getEntityClass());
		if (null != e){
			return e;
		}else{
			throw new EntityNotFoundException("object with id "+ i + "not found.");
		}
	}


	@DELETE
	@Path(ENTITY_QUERY)
	public void deleteEntity(@PathParam("id") String id) {
		E e = dao.findById(Long.parseLong(id),getEntityClass());
		if (e != null)
			dao.remove(e,getEntityClass());
	}

}
