package com.johannbarbie.persistance.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.identity.LongIdentity;

import com.johannbarbie.persistance.PersistenceConfiguration;
import com.johannbarbie.persistance.exceptions.EntityNotFoundException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;
import com.johannbarbie.persistance.exceptions.PersistanceException;


public class GenericRepository {
	
	public static final String OBJECT_QUERY_PARAM = "objectQuery";

	protected PersistenceManagerFactory pmf = null;
	protected PersistenceManager pm = null;
	protected Transaction tx = null;

	public GenericRepository() {
		pmf = PersistenceConfiguration.getInstance().getEntityManagerFactory();
	}

	public void map(Model oldEntity, Model newEntity) {
		oldEntity.update(newEntity);
	}

	public void persist(Model entity) throws PersistanceException {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		try {
			pm.makePersistent(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PersistanceException("persistance failed: "+ e.getMessage());
		}
	}

	public <K extends Model> void remove(K entity, Class<K> entityClass) {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
//		try {
			pm.deletePersistent(pm.getObjectById(new LongIdentity(entityClass,
					entity.getId())));
//		} finally {
//			pm.close();
//		}
	}

	public Model merge(Model entity) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public <K extends Model> void update(K entity, Class<K> entityClass)
			throws EntityNotFoundException, ParameterMissingException {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		K e = null;
		Transaction tx = pm.currentTransaction();
		if (entity.getId() != null) {
			try {
				tx.begin();
				LongIdentity idx = new LongIdentity(entityClass, entity.getId());
				Object[] o = pm.getObjectsById(idx);
				if (o.length > 0) {
					e = (K) o[0];
					map(e, entity);
				} else {
					throw new EntityNotFoundException("empty result set");
				}
				tx.commit();
			} finally {
				if (tx.isActive()) {
					tx.rollback();
				}

				pm.close();
			}
		} else {
			throw new ParameterMissingException("id = 0");
		}
	}

	// retrieve object, changes to object will not be persisted
	@SuppressWarnings("unchecked")
	public <K extends Model> K findById(Long id, Class<K> entityClass)
			throws EntityNotFoundException, ParameterMissingException {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		K e = null;
		if (id != null) {
			try {
				LongIdentity idx = new LongIdentity(entityClass, id);
				Object[] o = pm.getObjectsById(idx);
				e = (K) pm.detachCopy(o[0]);
			}catch (Exception ex) {
				return null;
				//throw new PersistanceException("could not communicate with data store:" + ex.getMessage());
			} finally {
				pm.close();
			}
		} else {
			throw new ParameterMissingException("id = 0");
		}
		return e;
	}

	public Model flush(Model entity) {
		throw new UnsupportedOperationException();
	}

	// todo: implement paging
	@SuppressWarnings("unchecked")
	public <K extends Model> List<K> findAll(Class<K> entityClass) {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		Query q = pm.newQuery(entityClass);
		// q.setOrdering("date desc");

		List<K> results = null;
		List<K> detached = null;
		try {
			results = (List<K>) q.execute();
			detached = new ArrayList<K>(results.size());
			for (K e : results) {
				detached.add(pm.detachCopy(e));
			}
		} finally {
			q.close(entityClass);
		}
		return detached;
	}

	public Model detach(Model m) {
		if (pm != null) {
			return pm.detachCopy(m);
		}
		return null;
	}

	public Integer removeAll() {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	public <K extends Model> Long queryWithParamAttached(List<K> attached, Map<String,String> queryParams,
			Integer offset, Integer limit, Class<K> entityClass) {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		if (null == offset) {
			offset = 0;
		}
		if (null == limit || limit == 0) {
			limit = 10;
		}
		Query q = pm.newQuery(entityClass);
		String filter = "id >=" + offset;
		if (null != queryParams)
			for (Entry<String,String> e : queryParams.entrySet()){
				//TODO: check for String sanity
				filter = e.getKey() + " == \"" + e.getValue() + "\" && " + filter;
			}
		q.setFilter(filter);
		q.setOrdering("id asc");
		q.getFetchPlan().setFetchSize(limit + 1);
		Long rv = null;
		List<K> results = null;
		try {
			results = (List<K>) q.execute();
			int i = 0;
			for (K e : results) {
				if (i >= limit) {
					return e.getId();
				} else {
					attached.add(e);
				}
				i++;
			}
		} finally {
			q.closeAll();
			//pm.close();
		}
		return rv;
	}

	@SuppressWarnings("unchecked")
	public <K extends Model> Long queryWithParam(List<K> detached, Map<String,String> queryParams,
			Integer offset, Integer limit, Class<K> entityClass) {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		if (null == offset) {
			offset = 0;
		}
		if (null == limit || limit == 0) {
			limit = 10;
		}
		Query q = pm.newQuery(entityClass);
		String filter = "id >=" + offset;
		if (null!=queryParams)
			for (Entry<String,String> e : queryParams.entrySet()){
				//TODO: check for String sanity
				filter = e.getKey() + " == \"" + e.getValue() + "\" && " + filter;
			}
		q.setFilter(filter);
		q.setOrdering("id asc");
		q.getFetchPlan().setFetchSize(limit + 1);
		Long rv = null;
		List<K> results = null;
		try {
			results = (List<K>) q.execute();
			int i = 0;
			for (K e : results) {
				if (i >= limit) {
					return e.getId();
				} else {
					detached.add(pm.detachCopy(e));
				}
				i++;
			}
		} finally {
			q.closeAll();
			//pm.close();
		}
		return rv;
	}
	
	
	@SuppressWarnings("unchecked")
	public <K extends Model> Long queryWithObjectParam(List<K> detached, Map<String,String> queryParams,
			Integer offset, Integer limit, Class<K> entityClass, Model m, Class<? extends Model> clazz) throws ParameterMissingException {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		if (null == offset) {
			offset = 0;
		}
		if (null == limit || limit == 0) {
			limit = 10;
		}
		Query q = pm.newQuery(entityClass);
		String filter = "id >=" + offset;
		if (null != queryParams)
			for (Entry<String,String> e : queryParams.entrySet()){
				//TODO: check for String sanity
				if (e.getKey().equalsIgnoreCase(OBJECT_QUERY_PARAM)){
					if (m!=null){
						q.declareParameters(clazz.getSimpleName() + " objectO");
						q.declareImports("import " + clazz.getName() + ";");
						filter = e.getValue() + " == objectO && " + filter;
						q.getFetchPlan().setGroup(e.getValue());
					}else{
						throw new ParameterMissingException("no object for object query provided");
					}
				}else{
					filter = e.getKey() + " == \"" + e.getValue() + "\" && " + filter;
				}
			}
		q.setFilter(filter);
		q.setOrdering("id asc");
		q.getFetchPlan().setFetchSize(limit + 1);
		Long rv = null;
		List<K> results = null;
		try {
			results = (List<K>) q.execute(m);
			int i = 0;
			for (K e : results) {
				if (i >= limit) {
					return e.getId();
				} else {
					detached.add(pm.detachCopy(e));
				}
				i++;
			}
		} finally {
			q.closeAll();
			//pm.close();
		}
		return rv;
	}

	// retrieve object, variable fetch depht
	@SuppressWarnings("unchecked")
	public <K extends Model> K findByIdAttached(Long id, Class<K> fetchClass)
			throws EntityNotFoundException, ParameterMissingException {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		K e = null;
		if (id != null) {
			try {
				LongIdentity idx = new LongIdentity(fetchClass, id);
				Object[] o = pm.getObjectsById(idx);
				e = (K) o[0];
			} catch (Exception ex) {
				throw new EntityNotFoundException(ex.getMessage());
			}
		} else {
			pm.close();
			throw new ParameterMissingException("id == null");
		}
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public <K extends Model> K findByIdAttachednoSession(Long id, Class<K> fetchClass)
			throws EntityNotFoundException, ParameterMissingException {
		if (null == pm || pm.isClosed()) {
			pm = pmf.getPersistenceManager();
		}
		K e = null;
		if (id != null) {
			try {
				LongIdentity idx = new LongIdentity(fetchClass, id);
				Object[] o = pm.getObjectsById(idx);
				e = (K) o[0];
				//tx.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
				pm.close();
				throw new EntityNotFoundException("empty result set");
			}
		} else {
			pm.close();
			throw new ParameterMissingException("id = 0");
		}
		return e;
	}

	
	// finish the transcation opened in findbyidattached
	public void commitTransaction() throws PersistanceException {
		boolean roleback = false;
		if (tx != null && tx.isActive()) {
			try {
				tx.commit();
			} finally {
				if (tx.isActive()) {
					tx.rollback();
					roleback = true;
				}
			}
		}
		if (roleback)
			throw new PersistanceException("commiting Transaction failed, rolled back!");
	}
	
	// finish the transcation opened in findbyidattached
	public void stopAttach() throws PersistanceException {
		boolean roleback = false;
		if (tx != null && tx.isActive()) {
			try {
				tx.commit();
			} finally {
				if (tx.isActive()) {
					tx.rollback();
					roleback = true;
				}
				pm.close();
			}
		}
		if (roleback)
			throw new PersistanceException("detatching objects failed, rolled back!");
	}
}
