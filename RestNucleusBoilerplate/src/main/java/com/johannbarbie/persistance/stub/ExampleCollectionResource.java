package com.johannbarbie.persistance.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import com.johannbarbie.persistance.dao.GenericRepository;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;
import com.johannbarbie.persistance.resources.AbstractCollectionResource;

@Path(ExampleCollectionResource.PATH)
public class ExampleCollectionResource extends AbstractCollectionResource<Example> {
	public static final String PATH = "/example2"; 
	public static final String PAGINATION = "/offset/{offset}/limit/{limit}"; 
	public static final String PATH_PAGINATION = PATH+PAGINATION; 
		
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
	@SuppressWarnings("rawtypes")
	@PUT
	public List<Object> update(Example e) throws Exception {
		if (e == null){
			throw new ParameterMissingException("no query object provided");
		}else{
			e = getDao().getObjectById(e.getId(), Example.class);
		}
		Map<String, String> customParams = new HashMap<String, String>();
		customParams.put(GenericRepository.OBJECT_QUERY_PARAM, "child");
		List<Example> fetched = new ArrayList<Example>();
		Query q = getDao().createObjectQuery(customParams, 0, 10,
				getEntityClass(),e,Example.class);
		Long i = getDao().query(fetched, e,10,q);
		
		List<Object> rv = new ArrayList<Object>();
		if (null!=i){
			rv.add(fetched);
			rv.add(new AbstractCollectionResource.NextOffset(i));
		}else{
			for (Example ex : fetched)
				rv.add(ex);
		}
		return rv;
	}
}