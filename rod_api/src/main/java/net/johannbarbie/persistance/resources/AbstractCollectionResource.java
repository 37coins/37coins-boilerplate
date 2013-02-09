package net.johannbarbie.persistance.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.johannbarbie.persistance.dao.Model;
import net.johannbarbie.persistance.exceptions.EntityNotFoundException;
import net.johannbarbie.persistance.exceptions.IdConflictException;
import net.johannbarbie.persistance.exceptions.ParameterMissingException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

public abstract class AbstractCollectionResource<E extends Model> extends
		AbstractAuthResource {

	protected abstract Class<E> getEntityClass();

	@SuppressWarnings("unchecked")
	@Post("json")
	public long create(E e) throws Exception {
		E ui = null;
		try {
			ui = (E) dao.findById(e.getId(), getEntityClass());
		} catch (ParameterMissingException ex) {
		} catch (EntityNotFoundException ex2) {
		}
		if (ui == null) {
			dao.persist(e);
			long rv = e.getId();
			dao.stopAttach();
			return rv;
		} else {
			throw new IdConflictException("id:" + e.getId());
		}
	}

	@Get
	public List<Object> getAll() throws Exception {
		// TODO: handle multiple parameters
		Map<String, String> some = getQuery().getValuesMap();
		for (Entry<String, String> e : some.entrySet()) {
			Boolean isQueryParam = true;
			for (Entry<String, String> a : authTokens.entrySet())
				if (a.getKey().equalsIgnoreCase(e.getKey()) || e.getKey().equalsIgnoreCase("authenticate"))
					isQueryParam = false;
			if (isQueryParam)
				customParams.put(e.getKey(), e.getValue());
		}

		List<E> detached = new ArrayList<E>((limit==null)?10:limit);
		Long i = dao.queryWithParam(detached, customParams, offset, limit,
				getEntityClass());
		List<Object> rv = new ArrayList<Object>();
		if (null!=i){
			rv.add(detached);
			rv.add(new NextOffset(i));
		}else{
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
