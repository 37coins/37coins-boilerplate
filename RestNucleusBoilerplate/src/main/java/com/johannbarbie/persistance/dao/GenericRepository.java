package com.johannbarbie.persistance.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.identity.LongIdentity;

import com.johannbarbie.persistance.PersistenceConfiguration;
import com.johannbarbie.persistance.exceptions.EntityNotFoundException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;
import com.johannbarbie.persistance.exceptions.PersistanceException;

/**
 * A data access object offering most common CRUD operations, queries and paging on JDO.
 * 
 * @author johba
 */
public class GenericRepository {
	public static final String PARAM_NAME = "genericRepo";
	public static final String OBJECT_QUERY_PARAM = "objectQuery";

	protected PersistenceManagerFactory pmf = null;
	protected PersistenceManager pm = null;

	// for testing with mock objects
	public GenericRepository(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	public GenericRepository() {
		this.pmf = PersistenceConfiguration.getInstance()
				.getEntityManagerFactory();
	}

	/*
	 * get a persistencemanager to use the datastore
	 */
	public void getPersistenceManager() {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
	}

	/*
	 * this has to be called after none of the handled objects are required any
	 * more.
	 */
	public void closePersistenceManager() {
		if (null != pm && !pm.isClosed()) {
			pm.close();
		}
	}

	public <K extends Model> K detach(K k, Class<K> entityClass) {
		getPersistenceManager();
		return pm.detachCopy(k);
	}

	public Model detach(Model m) {
		getPersistenceManager();
		return pm.detachCopy(m);
	}

	public <K extends Model> Collection<K> detach(Collection<K> k, Class<K> entityClass) {
		getPersistenceManager();
		return pm.detachCopyAll(k);
	}

	private void handleException(Exception e) {
		closePersistenceManager();
		e.printStackTrace();
		throw new PersistanceException(e.getMessage());
	}

	/*
	 * #############################
	 * 
	 * CRUD Opeations
	 */
	public void add(Model entity) {
		getPersistenceManager();
		try {
			pm.makePersistent(entity);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public <K extends Model> boolean existsObject(Long id, Class<K> entityClass) {
		K rv = getObjectById(id, false, entityClass);
		return (null!=rv);
	}
	
	public <K extends Model> K getObjectById(Long id, Class<K> entityClass) {
		return getObjectById(id, true, entityClass);
	}

	@SuppressWarnings("unchecked")
	private <K extends Model> K getObjectById(Long id, boolean validate, Class<K> entityClass) {
		getPersistenceManager();
		if (null == id)
			throw new ParameterMissingException("id == null");
		try {
			return (K) pm.getObjectById(new LongIdentity(entityClass, id),validate);
		}catch (JDOObjectNotFoundException e1){
			closePersistenceManager();
			throw new EntityNotFoundException("No entity found with id: "+id);
		}catch (Exception e) {
			handleException(e);
		}
		return null;
	}

	public <K extends Model> void update(K entity, Class<K> entityClass) {
		K rv = getObjectById(entity.getId(), entityClass);
		rv.update(entity);
	}

	public <K extends Model> void delete(Long id, Class<K> entityClass) {
		getPersistenceManager();
		try {
			pm.deletePersistent(pm.getObjectById(new LongIdentity(entityClass, id)));
		} catch (Exception e) {
			handleException(e);
		}
	}

	public <K extends Model> Query createParamQuery(
			Map<String, String> queryParams, Integer offset, Integer limit,
			Class<K> entityClass) {
		getPersistenceManager();
		offset = (null == offset) ? 0 : offset;
		limit = (null == limit) ? 0 : limit;
		Query q = pm.newQuery(entityClass);
		String filter = "id >=" + offset;
		if (null != queryParams)
			for (Entry<String, String> e : queryParams.entrySet()) {
				// TODO: check for String sanity
				filter = e.getKey() + " == \"" + e.getValue() + "\" && "
						+ filter;
			}
		q.setFilter(filter);
		q.setOrdering("id asc");
		q.getFetchPlan().setFetchSize(limit + 1);
		return q;
	}

	public <K extends Model> Query createObjectQuery(
			Map<String, String> queryParams, Integer offset, Integer limit,
			Class<K> entityClass, Model m, Class<? extends Model> clazz) {
		getPersistenceManager();
		offset = (null == offset) ? 0 : offset;
		limit = (null == limit) ? 0 : limit;
		Query q = pm.newQuery(entityClass);
		String filter = "id >=" + offset;
		if (null != queryParams)
			for (Entry<String, String> e : queryParams.entrySet()) {
				// TODO: check for String sanity
				if (e.getKey().equalsIgnoreCase(OBJECT_QUERY_PARAM)) {
					if (m != null) {
						q.declareParameters(clazz.getSimpleName() + " objectO");
						q.declareImports("import " + clazz.getName() + ";");
						filter = e.getValue() + " == objectO && " + filter;
						q.getFetchPlan().setGroup(e.getValue());
					} else {
						throw new ParameterMissingException(
								"no object for object query provided");
					}
				} else {
					filter = e.getKey() + " == \"" + e.getValue() + "\" && "
							+ filter;
				}
			}
		q.setFilter(filter);
		q.setOrdering("id asc");
		q.getFetchPlan().setFetchSize((int) (limit + 1));
		return q;
	}

	public <K extends Model> Long query(List<K> rv, Model m,Integer limit, Query q){
		getPersistenceManager();
		Long nextOffset = null;
		try {
			@SuppressWarnings("unchecked")
			List<K> results = (List<K>) ((null==m)?q.execute():q.execute(m));
			int i = 0;
			for (K e : results) {
				if (i >= limit) {
					return e.getId();
				} else {
					rv.add(e);
				}
				i++;
			}
		} catch (Exception e) {
			handleException(e);
		}
		return nextOffset;
	}
}
