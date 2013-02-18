package org.restnucleus.stub;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.restnucleus.dao.RNQuery;
import org.restnucleus.resources.AbstractCollectionResource;


/**
 * an example implementation of a collection resource
 * @author johba
 */
@Path(ExampleCollectionResource.PATH)
public class ExampleCollectionResource extends AbstractCollectionResource<Example> {
	public static final String PATH = "/example"; 
		
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
	@GET
	@Path("/offset/{offset}/limit/{limit}")
	public List<Example> getFromCollectionWithPagination(@PathParam("offset") String offset,
			@PathParam("limit") String limit) {
		Long o = null;
		if (null!=offset)
			o = Long.parseLong(offset);
		Long l = null;
		if (null!=limit)
			l = Long.parseLong(limit);
		RNQuery q = getQuery();
		q.setRange(o % l, l);
		return getDao().queryList(q,getEntityClass());
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