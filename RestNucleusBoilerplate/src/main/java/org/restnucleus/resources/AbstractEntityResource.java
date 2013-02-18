package org.restnucleus.resources;

import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.restnucleus.dao.Model;


/**
 * An abstract implementation of operations on an entity.
 * 
 * @author johba
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractEntityResource<E extends Model> extends
		AbstractResource {
	public static final String ENTITY_QUERY = "/{id}";

	protected abstract Class<E> getEntityClass();

	@PUT
	public Long updateEntity(InputStream requestBodyStream, @PathParam("id") String id) {
		E e = parse (requestBodyStream,getEntityClass());
		e.setId(Long.parseLong(id));
		getDao().update(e, getEntityClass());
		Long rv = e.getId();
		return rv;
	}

	@GET
	public E getEntity(@PathParam("id") String id) {
		Long i = Long.parseLong(id);
		E e = getDao().getObjectById(i, getEntityClass());
		return e;
	}

	@DELETE
	public void deleteEntity(@PathParam("id") String id) {
		getDao().delete(Long.parseLong(id), getEntityClass());
	}

}
