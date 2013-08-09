package org.restnucleus.resources;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.restnucleus.dao.Example;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.RNQuery;

import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;


/**
 * an example implementation of a collection resource
 * @author johba
 */
@Api(value = ExampleResource.PATH, description = "an example implementation of a resource")
@Path(ExampleResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {
	public static final String PATH = "/example"; 
	public static final String ENTITY_QUERY = "/{id}";
	public static final String PATH_ENTITY = PATH+ENTITY_QUERY;
		
	@Inject protected GenericRepository dao;
	
	@Inject protected RNQuery query;


	@POST
	@ApiOperation(value = "Create Entity on Collection.", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 409, reason = "Object contains id already.")})
	@ApiParamsImplicit({ @ApiParamImplicit(value = "Example object that needs to be added to the store", required = true, dataType = "Example", paramType = "body") })
	public long createOnCollection(Example e) {
		dao.add(e);
		return e.getId();
	}

	@GET
	@ApiOperation(value = "Query Collection for Entity.", notes = "generic implementation", responseClass = "List[org.restnucleus.dao.Example]")
	@ApiParamsImplicit({ 
		@ApiParamImplicit(name=RNQuery.PAGE, value=RNQuery.PAGE_DESC, defaultValue=""+RNQuery.DEF_PAGE, dataType="long", paramType="query"),
		@ApiParamImplicit(name=RNQuery.SIZE, value=RNQuery.SIZE_DESC, allowableValues = "range[1,"+RNQuery.MAX_PAGE_SIZE+"]", dataType="long", paramType="query"),
		@ApiParamImplicit(name=RNQuery.FILTER, value=RNQuery.FILTER_DESC, dataType="string", paramType="query"),
		@ApiParamImplicit(name=RNQuery.SORT, value=RNQuery.SORT_DESC, dataType="string", paramType="query")})
	public List<Example> getFromCollection() {
		List<Example> rv = dao.queryList(query, Example.class);
		return rv;
	}
	
	@DELETE
	@ApiOperation(value = "Query Delete on Example Collection.", notes = "use any possible query")
	@ApiParamsImplicit({ 
		@ApiParamImplicit(name=RNQuery.FILTER, value=RNQuery.FILTER_DESC, dataType="String", paramType="query")})
	public void delete(){
		dao.queryDelete(query, Example.class);
	}
	
	@PUT
	@Path(ENTITY_QUERY)
	@ApiOperation(value = "Update Entity by ID", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 404, reason = "Object not found")})
	@ApiParamsImplicit({ @ApiParamImplicit(value = "Object to replace current Object with.", required = true, dataType = "Example") })
	public Long updateEntity(
			Example e,
			@ApiParam(value = "ID of object to be fetched", required = true)
			@PathParam("id") String id) {
		e.setId(Long.parseLong(id));
		dao.update(e, Example.class);
		Long rv = e.getId();
		return rv;
	}

	@GET
	@Path(ENTITY_QUERY)
	@ApiOperation(value = "Find Entity by ID", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 404, reason = "Object not found")})
	public Example getEntity(
			@ApiParam(value = "ID of object to be fetched", required = true)
			@PathParam("id") String id) {
		Long i = Long.parseLong(id);
		Example e = dao.getObjectById(i, Example.class);
		return e;
	}

	@DELETE
	@Path(ENTITY_QUERY)
	@ApiOperation(value = "Delete Entity by ID", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 404, reason = "Object not found")})
	public void deleteEntity(
			@ApiParam(value = "ID of object to be deleted", required = true)
			@PathParam("id") String id) {
		dao.delete(Long.parseLong(id), Example.class);
	}
	/*
	 * notice the object query by put, not sure this is a good idea though
	 */
	@PUT
	@ApiOperation(value = "query by object", notes = "implements an object query, providing the object through put.")
	@ApiParamsImplicit({ @ApiParamImplicit(value = "query object that shall be used for query", required = true, dataType = "Example", paramType = "body") })
	public List<Example> update(Example e) throws Exception {
		if (e == null){
			throw new WebApplicationException(
					"no query object provided",
					Response.Status.BAD_REQUEST);
		}
		query.addQueryObject("child", e);
		return dao.queryList(query,Example.class);
	}
}