package com.johannbarbie.persistance.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.jdo.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.IdConflictException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;

@Produces("application/json")    
public abstract class AbstractCollectionResource<E extends Model> extends
		AbstractResource {

	protected abstract Class<E> getEntityClass();

	@POST
	@Consumes("application/json")
	public long createOnCollection(InputStream requestBodyStream) {
		dao.getPersistenceManager();
		ObjectMapper om = new ObjectMapper();
		E e;
		try {
			e = om.readValue(requestBodyStream, getEntityClass());
		} catch (Exception e1) {
			throw new ParameterMissingException("can not be parsed.");
		}
		if (null == e.getId()) {
			//TODO: check for Object relationships;
			//we don't want chained persistance, but all children should exist already
			dao.add(e);
			long rv = e.getId();
			dao.closePersistenceManager();
			return rv;
		} else {
			dao.closePersistenceManager();
			throw new IdConflictException("object contains id already!");
		}
	}
	
	@GET
	@Path("/offset/{offset}/limit/{limit}")
	public List<Object> getFromCollectionWithPagination(@Context UriInfo uriInfo,@PathParam("offset") String offset,@PathParam("limit") String limit) {
		Integer o = Integer.parseInt(offset);
		Integer l = Integer.parseInt(limit);
		if (l < 0 )
			throw new ParameterMissingException("bla");
		return getFromCollection2(uriInfo,o,l);
	}

	@GET
	public List<Object> getFromCollection(@Context UriInfo uriInfo) {
		Integer o = 0;
		Integer l = 10;
		return getFromCollection2(uriInfo,o,l);
	}
	
	public List<Object> getFromCollection2(UriInfo uriInfo,Integer o, Integer l) {
		dao.getPersistenceManager();
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
		Query q = dao.createParamQuery(customParams, o, l, getEntityClass());
		Long i = dao.query(detached, null, l, q);
		List<Object> rv = new ArrayList<Object>();
		if (null!=i){
			rv.add(dao.detach(detached,getEntityClass()));
			rv.add(new NextOffset(i));
		}else{
			for (E e : detached)
				rv.add(dao.detach(e,getEntityClass()));
		}
		dao.closePersistenceManager();
		return rv;
	}

	public class NextOffset {
		public Long nextOffset;

		public NextOffset(Long nextOffset) {
			this.nextOffset = nextOffset;
		}
	}

}
