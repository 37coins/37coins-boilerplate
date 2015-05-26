package org.restnucleus.resources;

import org.restnucleus.RNQueryBean;
import org.restnucleus.dao.Example;
import org.restnucleus.dao.GenericRepository;

import javax.inject.Inject;
import javax.jdo.JDOObjectNotFoundException;
import javax.ws.rs.BeanParam;
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
import java.util.List;


/**
 * an example implementation of a collection resource
 *
 * @author johba
 */
@Path(ExampleResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {
    public static final String PATH = "/example";
    public static final String ENTITY_QUERY = "/{id}";
    public static final String PATH_ENTITY = PATH + ENTITY_QUERY;

    private final GenericRepository dao;

    @Inject
    public ExampleResource(GenericRepository dao) {
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
        if (null == test)
            throw new WebApplicationException("empty", Response.Status.BAD_REQUEST);
        return "hallo " + test;
    }

    @GET
    public List<Example> getFromCollection(@BeanParam RNQueryBean query) {
        return dao.queryList(query.create(), Example.class);
    }

    @DELETE
    public void delete(@BeanParam RNQueryBean query) {
        dao.queryDelete(query.create(), Example.class);
    }

    @PUT
    @Path(ENTITY_QUERY)
    public Long updateEntity(Example e, @PathParam("id") String id) {
        long eId = Long.parseLong(id);
        e.setId(eId);
        dao.update(e, Example.class);
        return eId;
    }

    @GET
    @Path(ENTITY_QUERY)
    public Example getEntity(@PathParam("id") String id) {
        try {
            return dao.getObjectById(Long.parseLong(id), Example.class);
        } catch (JDOObjectNotFoundException ex) {
            throw new WebApplicationException(ex, Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path(ENTITY_QUERY)
    public void deleteEntity(@PathParam("id") String id) {
        dao.delete(Long.parseLong(id), Example.class);
    }
}