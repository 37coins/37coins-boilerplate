package com.johannbarbie.persistance.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.EntityNotFoundException;
import com.johannbarbie.persistance.exceptions.IdConflictException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;

@Produces("application/json")    
public abstract class AbstractCollectionResource<E extends Model> extends
		AbstractResource {

	protected abstract Class<E> getEntityClass();

	@POST
	@Consumes("application/json")
	public long createOnCollection(E e) {
		E ui = null;
		if (null!=e.getId())
			try {
				ui = dao.findById(e.getId(), getEntityClass());
			} catch (ParameterMissingException ex) {
			} catch (EntityNotFoundException ex2) {
			}
		if (ui == null) {
			dao.persist(e);
			long rv = e.getId();
			dao.stopAttach();
			return rv;
		} else {
			throw new IdConflictException("id:" + e.getId());
		}
	}
	
	@GET
	@Path("/offset/{offset}/limit/{limit}")
	public List<Object> getFromCollectionWithPagination(@Context UriInfo uriInfo,@PathParam("offset") String offset,@PathParam("limit") String limit) {
		Long o = Long.parseLong(offset);
		Long l = Long.parseLong(limit);
		if (l < 0 )
			throw new ParameterMissingException("bla");
		return getFromCollection2(uriInfo,o,l);
	}

	@GET
	public List<Object> getFromCollection(@Context UriInfo uriInfo) {
		Long o = 0L;
		Long l = 10L;
		return getFromCollection2(uriInfo,o,l);
	}
	
	public List<Object> getFromCollection2(UriInfo uriInfo,Long o, Long l) {
		// TODO: handle multiple parameters
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		for (Entry<String, List<String>> e : queryParams.entrySet()) {
			Boolean isQueryParam = true;
//			for (Entry<String, String> a : authTokens.entrySet())
//				if (a.getKey().equalsIgnoreCase(e.getKey()) || e.getKey().equalsIgnoreCase("authenticate"))
//					isQueryParam = false;
			if (isQueryParam)
				customParams.put(e.getKey(), e.getValue().get(0));
		}

		List<E> detached = new ArrayList<E>();
		Long i = dao.queryWithParam(detached, customParams, o, l,
				getEntityClass());
		List<Object> rv = new ArrayList<Object>();
		if (null!=i){
			rv.add(detached);
			rv.add(new NextOffset(i));
		}else{
			for (E e : detached)
				rv.add(e);
		}
		return rv;
	}

	public class NextOffset {
		public Long nextOffset;

		public NextOffset(Long nextOffset) {
			this.nextOffset = nextOffset;
		}
	}

}
