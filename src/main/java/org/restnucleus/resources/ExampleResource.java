package org.restnucleus.resources;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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

import com.google.inject.servlet.RequestScoped;


/**
 * an example implementation of a collection resource
 * @author johba
 */
@RequestScoped
@Path(ExampleResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {
	public static final String PATH = "/example"; 
	public static final String ENTITY_QUERY = "/{id}";
	public static final String PATH_ENTITY = PATH+ENTITY_QUERY;
	
	private final RNQuery query;
	
	private final GenericRepository dao;
	
	@Inject public ExampleResource(ServletRequest request, GenericRepository dao) {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		query = (RNQuery)httpReq.getAttribute(RNQuery.QUERY_PARAM);
		this.dao = dao;
	}

	@POST
	public long createOnCollection(Example e) {
		dao.add(e);
		return e.getId();
	}
	
	@POST
	@Path("/formTest")
	public String formTest(@FormParam("test") String test) {
		if (null==test)
			throw new WebApplicationException("empty", Response.Status.BAD_REQUEST);
		return "hallo " + test;
	}

	@GET
	public List<Example> getFromCollection() {
		List<Example> rv = dao.queryList(query, Example.class);
		return rv;
	}
	
	@DELETE
	public void delete(){
		dao.queryDelete(query, Example.class);
	}
	
	@PUT
	@Path(ENTITY_QUERY)
	public Long updateEntity(Example e, @PathParam("id") String id) {
		e.setId(Long.parseLong(id));
		dao.update(e, Example.class);
		Long rv = e.getId();
		return rv;
	}

	@GET
	@Path(ENTITY_QUERY)
	public Example getEntity(@PathParam("id") String id) {
		Long i = Long.parseLong(id);
		Example e = null;
		try {
			e = dao.getObjectById(i, Example.class);
		}catch(JDOObjectNotFoundException ex){
			throw new WebApplicationException(ex, Response.Status.NOT_FOUND);
		}
		return e;
	}

	@DELETE
	@Path(ENTITY_QUERY)
	public void deleteEntity(@PathParam("id") String id) {
		dao.delete(Long.parseLong(id), Example.class);
	}
}