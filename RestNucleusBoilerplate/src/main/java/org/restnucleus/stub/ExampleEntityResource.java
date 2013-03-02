package org.restnucleus.stub;

import javax.ws.rs.Path;

import org.restnucleus.resources.AbstractEntityResource;

import com.wordnik.swagger.annotations.Api;


/**
 * 
 * @author johba
 *
 */
@Api(value = ExampleEntityResource.PATH, description = "an example implementation of a entity resource")
@Path(ExampleEntityResource.PATH)
public class ExampleEntityResource extends AbstractEntityResource<Example> {
	public static final String PATH = "/example";
	public static final String PATH_ENTITY = PATH + ENTITY_QUERY;
	
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
}