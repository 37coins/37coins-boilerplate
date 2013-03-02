package org.restnucleus.resources;

import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.restnucleus.dao.Model;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;


/**
 * An abstract implementation of operations on an entity.
 * 
 * @author johba
 */
public abstract class AbstractEntityResource<E extends Model> extends
		AbstractResource {
	public static final String ENTITY_QUERY = "/{id}";

	protected abstract Class<E> getEntityClass();

	@PUT
	@Path(ENTITY_QUERY)
	@ApiOperation(value = "Update Entity by ID", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 404, reason = "Object not found")})
	@ApiParamsImplicit({ @ApiParamImplicit(value = "Object to replace current Object with.", required = true, dataType = "Example") })
	public Long updateEntity(
			InputStream requestBodyStream,
			@ApiParam(value = "ID of object to be fetched", required = true)
			@PathParam("id") String id) {
		E e = parse (requestBodyStream,getEntityClass());
		e.setId(Long.parseLong(id));
		getDao().update(e, getEntityClass());
		Long rv = e.getId();
		return rv;
	}

	@GET
	@Path(ENTITY_QUERY)
	@ApiOperation(value = "Find Entity by ID", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 404, reason = "Object not found")})
	public E getEntity(
			@ApiParam(value = "ID of object to be fetched", required = true)
			@PathParam("id") String id) {
		Long i = Long.parseLong(id);
		E e = getDao().getObjectById(i, getEntityClass());
		return e;
	}

	@DELETE
	@Path(ENTITY_QUERY)
	@ApiOperation(value = "Delete Entity by ID", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 404, reason = "Object not found")})
	public void deleteEntity(
			@ApiParam(value = "ID of object to be deleted", required = true)
			@PathParam("id") String id) {
		getDao().delete(Long.parseLong(id), getEntityClass());
	}

}
