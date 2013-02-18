package org.restnucleus.stub;

import javax.ws.rs.Path;

import org.restnucleus.resources.AbstractEntityResource;


/**
 * 
 * @author johba
 *
 */
@Path(ExampleEntityResource.PATH_ENTITY)
public class ExampleEntityResource extends AbstractEntityResource<Example> {
	public static final String PATH = "/example";
	public static final String PATH_ENTITY = PATH + ENTITY_QUERY;
	
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
}