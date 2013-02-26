package org.restnucleus.resources;

import java.io.InputStream;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.restlet.Request;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.Model;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.exceptions.ParameterMissingException;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.filter.SearchFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Resource helper Class
 * 
 * @author johba
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractResource {

	/*
	 * to parse representations of unknown type, we use the generic resource's
	 * type information here.
	 */
	protected <E extends Model> E parse(InputStream requestBodyStream,
			Class<E> clazz) {
		ObjectMapper om = new ObjectMapper();
		E e;
		try {
			e = om.readValue(requestBodyStream, clazz);
		} catch (Exception e1) {
			throw new ParameterMissingException("can not be parsed.");
		}
		return e;
	}

	/*
	 * retrieves the persistence manager from the thread's request object.
	 */
	protected GenericRepository getDao() {
		return (GenericRepository) Request.getCurrent().getAttributes()
				.get(ApplicationFilter.DAO_PARAM);
	}
	
	/*
	 * get the query object
	 */
	protected RNQuery getQuery() {
		RNQuery rv = (RNQuery) Request.getCurrent().getAttributes().get(SearchFilter.QUERY_PARAM);
		if (null==rv)
			rv = new RNQuery();
		return rv;
	}
}
