package org.restnucleus.stub;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restnucleus.dao.RNQuery;
import org.restnucleus.resources.AbstractCollectionResource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;


/**
 * an example implementation of a collection resource
 * @author johba
 */
@Api(value = ExampleCollectionResource.PATH, description = "an example implementation of a collection resource")
@Path(ExampleCollectionResource.PATH)
public class ExampleCollectionResource extends AbstractCollectionResource<Example> {
	public static final String PATH = "/examples"; 
		
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}


	@POST
	@Override
	@ApiOperation(value = "Create Entity on Collection.", notes = "generic implementation")
    @ApiErrors(value = { @ApiError(code = 409, reason = "Object contains id already.")})
	@ApiParamsImplicit({ @ApiParamImplicit(value = "Example object that needs to be added to the store", required = true, dataType = "Example", paramType = "body") })
	public long createOnCollection(
			InputStream requestBodyStream) {
		return super.createOnCollection(requestBodyStream);
	}

	@GET
	@Override
	@ApiOperation(value = "Query Collection for Entity.", notes = "generic implementation", responseClass = "List[org.restnucleus.stub.Example]")
	@ApiParamsImplicit({ 
		@ApiParamImplicit(name=RNQuery.PAGE_NAME, value=RNQuery.PAGE_DESC, defaultValue=""+RNQuery.DEF_PAGE_SIZE, dataType="long", paramType="query"),
		@ApiParamImplicit(name=RNQuery.SIZE_NAME, value=RNQuery.SIZE_DESC, allowableValues = "range[1,"+RNQuery.MAX_PAGE_SIZE+"]", dataType="long", paramType="query"),
		@ApiParamImplicit(name=RNQuery.BFORE_NAME, value=RNQuery.BFORE_DESC, dataType="Date", paramType="query"),
		@ApiParamImplicit(name=RNQuery.AFTER_NAME, value=RNQuery.AFTER_DESC, dataType="Date", paramType="query"),
		@ApiParamImplicit(name=RNQuery.FILTER_NAME, value=RNQuery.FILTER_DESC, dataType="string", paramType="query"),
		@ApiParamImplicit(name=RNQuery.SORT_NAME, value=RNQuery.SORT_DESC, dataType="string", paramType="query")})
	public List<Example> getFromCollection() {
		return super.getFromCollection();
	}
	
	@DELETE
	@ApiOperation(value = "Query Delete on Example Collection.", notes = "use any possible query")
	@ApiParamsImplicit({ 
		@ApiParamImplicit(name=RNQuery.BFORE_NAME, value=RNQuery.BFORE_DESC, dataType="Date", paramType="query"),
		@ApiParamImplicit(name=RNQuery.AFTER_NAME, value=RNQuery.AFTER_DESC, dataType="Date", paramType="query"),
		@ApiParamImplicit(name=RNQuery.FILTER_NAME, value=RNQuery.FILTER_DESC, dataType="String", paramType="query")})
	public void delete(){
		getDao().queryDelete(getQuery(), Example.class);
	}
	/*
	 * notice the object query by put, not sure this is a good idea though
	 */
//	@SuppressWarnings("rawtypes")
//	@PUT
//	public List<Object> update(Example e) throws Exception {
//		if (e == null){
//			throw new ParameterMissingException("no query object provided");
//		}else{
//			e = getDao().getObjectById(e.getId(), Example.class);
//		}
//		Map<String, String> customParams = new HashMap<String, String>();
//		customParams.put(GenericRepository.OBJECT_QUERY_PARAM, "child");
//		List<Example> fetched = new ArrayList<Example>();
//		Query q = getDao().createObjectQuery(customParams, 0, 10,
//				getEntityClass(),e,Example.class);
//		Long i = getDao().query(fetched, e,10,q);
//		
//		List<Object> rv = new ArrayList<Object>();
//		if (null!=i){
//			rv.add(fetched);
//			rv.add(new AbstractCollectionResource.NextOffset(i));
//		}else{
//			for (Example ex : fetched)
//				rv.add(ex);
//		}
//		return rv;
//	}
}