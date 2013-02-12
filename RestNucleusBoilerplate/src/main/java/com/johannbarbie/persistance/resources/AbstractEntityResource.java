package com.johannbarbie.persistance.resources;

import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;

@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractEntityResource<E extends Model> extends
		AbstractResource {
	public static final String ENTITY_QUERY = "/{id}";

	protected abstract Class<E> getEntityClass();

	@PUT
	@Path(ENTITY_QUERY)
	public Long updateEntity(InputStream requestBodyStream,
			@PathParam("id") String id) {
		dao.getPersistenceManager();
		ObjectMapper om = new ObjectMapper();
		E a;
		try {
			a = om.readValue(requestBodyStream, getEntityClass());
		} catch (Exception e1) {
			throw new ParameterMissingException("can not be parsed.");
		}
		a.setId(Long.parseLong(id));
		dao.update(a, getEntityClass());
		Long rv = a.getId();
		dao.closePersistenceManager();
		return rv;
	}

	@GET
	@Path(ENTITY_QUERY)
	public E getEntity(@PathParam("id") String id) {
		dao.getPersistenceManager();
		Long i = Long.parseLong(id);
		E e = dao.detach(dao.getObjectById(i, getEntityClass()),getEntityClass());
		dao.closePersistenceManager();
		return e;
	}

	@DELETE
	@Path(ENTITY_QUERY)
	public void deleteEntity(@PathParam("id") String id) {
		dao.getPersistenceManager();
		dao.delete(Long.parseLong(id), getEntityClass());
		dao.closePersistenceManager();
	}

}
