package com.johannbarbie.persistance.stub;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
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
	
	@Override
	@POST
	public long createOnCollection(Example e) {
		if (e.getChild()!=null){
			e.setChild(dao.findByIdAttached(e.getChild().getId(), Example.class));
			dao.commitTransaction();
		}
		return super.createOnCollection(e);
	}
	
	@SuppressWarnings("rawtypes")
	@PUT
	public List<Object> update(Example e) throws Exception {
		if (e == null){
			throw new ParameterMissingException("no query object provided");
		}else{
			e = dao.findByIdAttachednoSession(e.getId(), Example.class);
		}
		customParams.put(GenericRepository.OBJECT_QUERY_PARAM, "child");
		List<Example> detached = new ArrayList<Example>();
		Long i = dao.queryWithObjectParam(detached, customParams, 0L, 10L,
				getEntityClass(),e,Example.class);
		
		List<Object> rv = new ArrayList<Object>();
		if (null!=i){
			rv.add(detached);
			rv.add(new AbstractCollectionResource.NextOffset(i));
		}else{
			for (Example ex : detached)
				rv.add(ex);
		}
		return rv;
	}
}