package net.johannbarbie.persistance.stub;

import net.johannbarbie.persistance.resources.AbstractEntityResource;

public class ExampleEntityResource extends AbstractEntityResource<Example> {
	
	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}
	
}