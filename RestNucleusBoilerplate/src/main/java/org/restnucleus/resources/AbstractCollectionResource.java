package org.restnucleus.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.restnucleus.dao.Model;
import org.restnucleus.exceptions.IdConflictException;

import com.wordnik.swagger.annotations.ApiParam;


/**
 * An abstract implementation of operations on a collection.
 * @author johba
 */

public abstract class AbstractCollectionResource<E extends Model> extends
		AbstractResource {

	protected abstract Class<E> getEntityClass();

	@Consumes(MediaType.APPLICATION_JSON)
	public long createOnCollection(
			@ApiParam(value = "Represesntation of Object to be created.", required = true)
			InputStream requestBodyStream) {
		E e = parse(requestBodyStream, getEntityClass());
		if (null != e.getId())
			throw new IdConflictException("object contains id already!");
		getDao().add(e);
		return e.getId();
	}

	public List<E> getFromCollection() {
		List<E> rv = getDao().queryList(getQuery(),getEntityClass());
		return rv;
	}

}
