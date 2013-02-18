package org.restnucleus.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.restnucleus.dao.Model;
import org.restnucleus.exceptions.IdConflictException;


/**
 * An abstract implementation of operations on a collection.
 * @author johba
 */

@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractCollectionResource<E extends Model> extends
		AbstractResource {

	protected abstract Class<E> getEntityClass();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public long createOnCollection(InputStream requestBodyStream) {
		E e = parse(requestBodyStream, getEntityClass());
		if (null != e.getId())
			throw new IdConflictException("object contains id already!");
		getDao().add(e);
		return e.getId();
	}

	@GET
	public List<E> getFromCollection() {
		return getDao().queryList(getQuery(),getEntityClass());
	}

}
