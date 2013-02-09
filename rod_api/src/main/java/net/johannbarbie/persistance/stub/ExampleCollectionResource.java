package net.johannbarbie.persistance.stub;

import java.util.ArrayList;
import java.util.List;

import net.johannbarbie.persistance.dao.GenericRepository;
import net.johannbarbie.persistance.exceptions.ParameterMissingException;
import net.johannbarbie.persistance.resources.AbstractCollectionResource;

import org.restlet.resource.Post;
import org.restlet.resource.Put;

public class ExampleCollectionResource extends AbstractCollectionResource<Example> {
	
	@Override
	public void doInit() {
		disableAuthentication();
		super.doInit();
	}
	
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
	@Override
	@Post("json")
	public long create(Example e) throws Exception {
		if (e.getChild()!=null){
			e.setChild(dao.findByIdAttached(e.getChild().getId(), Example.class));
			dao.commitTransaction();
		}
		return super.create(e);
	}
	
	@SuppressWarnings("rawtypes")
	@Put("json")
	public List<Object> update(Example e) throws Exception {
		if (e == null){
			throw new ParameterMissingException("no query object provided");
		}else{
			e = dao.findByIdAttachednoSession(e.getId(), Example.class);
		}
		customParams.put(GenericRepository.OBJECT_QUERY_PARAM, "child");
		List<Example> detached = new ArrayList<Example>((limit==null)?10:limit);
		Long i = dao.queryWithObjectParam(detached, customParams, offset, limit,
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