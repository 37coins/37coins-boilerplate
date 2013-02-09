package net.johannbarbie.persistance.resources;

import net.johannbarbie.persistance.dao.Model;
import net.johannbarbie.persistance.exceptions.ParameterMissingException;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public abstract class AbstractEntityResource<E extends Model> extends
		AbstractAuthResource {
	
	protected abstract Class<E> getEntityClass();
	
	@Put("json")
	public Long update(E a) throws Exception {
		try {
			a.setId(id);
			dao.update(a,getEntityClass());
		} catch (ParameterMissingException e) {
		}
		return a.getId();
	}

	@SuppressWarnings("unchecked")
	@Get
	public E getEntity() throws Exception {
		return (E)dao.findById(id,getEntityClass());
	}

	@SuppressWarnings("unchecked")
	@Delete
	public void deleteEntity() throws Exception {
		E e = (E)dao.findById(id,getEntityClass());
		if (e != null)
			dao.remove(e,getEntityClass());
	}

}
