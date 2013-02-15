package com.johannbarbie.persistance.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.IdConflictException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;

@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractCollectionResource<E extends Model> extends
		AbstractResource {

	protected abstract Class<E> getEntityClass();

	@POST
	@Consumes("application/json")
	public long createOnCollection(InputStream requestBodyStream) {
		E e = parse(requestBodyStream, getEntityClass());
		if (null != e.getId())
			throw new IdConflictException("object contains id already!");
		getDao().add(e);
		long rv = e.getId();
		return rv;
	}

	@GET
	@Path("/offset/{offset}/limit/{limit}")
	public List<Object> getFromCollectionWithPagination(
			@Context UriInfo uriInfo, @PathParam("offset") String offset,
			@PathParam("limit") String limit) {
		Integer o = Integer.parseInt(offset);
		Integer l = Integer.parseInt(limit);
		if (l < 0)
			throw new ParameterMissingException("bla");
		return getFromCollection2(uriInfo, o, l);
	}

	@GET
	public List<Object> getFromCollection(@Context UriInfo uriInfo) {
		Integer o = 0;
		Integer l = 10;
		return getFromCollection2(uriInfo, o, l);
	}

	public List<Object> getFromCollection2(UriInfo uriInfo, Integer o, Integer l) {
		Map<String, String> queryParams = new HashMap<String, String>();
		MultivaluedMap<String, String> m = uriInfo.getQueryParameters();
		for (Entry<String, List<String>> e : m.entrySet()) {
			// TODO: handle multiple parameters
			queryParams.put(e.getKey(), e.getValue().get(0));
		}
		List<E> detached = new ArrayList<E>();
		Query q = getDao().createParamQuery(queryParams, o, l, getEntityClass());
		Long i = getDao().query(detached, null, l, q);
		List<Object> rv = new ArrayList<Object>();
		if (null != i) {
			rv.add(detached);
			rv.add(new NextOffset(i));
		} else {
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
