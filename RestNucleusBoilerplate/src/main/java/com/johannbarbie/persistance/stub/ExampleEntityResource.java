package com.johannbarbie.persistance.stub;

import javax.ws.rs.Path;

import com.johannbarbie.persistance.resources.AbstractEntityResource;

@Path(ExampleEntityResource.PATH)
public class ExampleEntityResource extends AbstractEntityResource<Example> {
	public static final String PATH = "/example";
	public static final String PATH_ENTITY = PATH + ENTITY_QUERY;
	
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
}