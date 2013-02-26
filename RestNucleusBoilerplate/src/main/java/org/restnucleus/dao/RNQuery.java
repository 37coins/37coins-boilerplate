package org.restnucleus.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * An instance of this is used to collect query information during request processing.
 * @author johann
 */
public class RNQuery {
	public final static long MAX_PAGE_SIZE = 1000;
	public final static long DEF_PAGE_SIZE = 10;
	public static final String BFORE_PARAM = "before";
	public static final String AFTER_PARAM = "after";
	
	private Map<String, String> filter = new HashMap<String,String>();

	private String ordering = null;

	private Long page = null;

	private Long size = null;
	
	private Map<String,Object> queryObjects = new HashMap<String,Object>();

	public String getJdoFilter() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> e: filter.entrySet()){
			if (sb.length() > 1)
				sb.append(" && ");
			sb.append(e.getKey() + " == '"+e.getValue()+"'");
		}
		return sb.toString();
	}
	
	public String getFilter(String key){
		return filter.get(key);
	}
	
	public RNQuery addFilter(String key, String value){
		filter.put(key, value);
		return this;
	}
	
	public boolean hasFilter(String key){
		return filter.containsKey(key);
	}
	
	public void clearFilter(){
		filter.clear();
	}

	public String getOrdering() {
		return ordering;
	}

	public RNQuery setOrdering(String ordering) {
		this.ordering = ordering;
		return this;
	}

	/*
	 * we handle pagination logic here
	 */
	public RNQuery setRange(Long page, Long size) {
		if (null==page || page < 0)
			this.page = 0L;
		else 
			this.page = page;
		if (null==size || size < 1)
			this.size = DEF_PAGE_SIZE;
		else if (size > MAX_PAGE_SIZE)
			this.size = MAX_PAGE_SIZE;
		else
			this.size = size;
		return this;
	}

	public Long getFrom() {
		if (null == page || null == size)
			return 0L;
		return page * size;
	}

	public Long getTo() {
		if (null == page || null == size)
			return DEF_PAGE_SIZE;
		return getFrom() + size;
	}

	public Long getPage() {
		if (null == page)
			return 0L;
		return this.page;
	}

	public Long getSize() {
		if (null == size)
			return DEF_PAGE_SIZE;
		return this.size;
	}
	
	public Query getJdoQ(PersistenceManager pm, Class<? extends Model> clazz){
		Query rv = pm.newQuery(clazz);
		rv.setFilter(this.getJdoFilter());
		rv.setRange(this.getFrom(), this.getTo());
		rv.setOrdering(this.getOrdering());
		if (queryObjects!=null){
			boolean atLeastOneDate = false;
			StringBuffer sb = new StringBuffer();
			sb.append(this.getJdoFilter());
			String params = "";
			if (queryObjects.containsKey(BFORE_PARAM)){
				atLeastOneDate = true;
				if (params.length()>3)
					params += ", ";
				params += "Date "+BFORE_PARAM;
				if (sb.length()>3)
					sb.append(" && ");
				sb.append("this.creationTime < ");
				sb.append(BFORE_PARAM);
			}
			if (queryObjects.containsKey(AFTER_PARAM)){
				atLeastOneDate = true;
				if (params.length()>3)
					params += ", ";
				params += "Date "+AFTER_PARAM;
				
				if (sb.length()>3)
					sb.append(" && ");
				sb.append("this.creationTime > ");
				sb.append(AFTER_PARAM);			
			}
			if (atLeastOneDate){
				rv.declareImports("import java.util.Date");
				rv.setFilter(sb.toString());
				if (params.length()>3)
					rv.declareParameters(params);
			}
		}
		// some optimizations
		rv.addExtension("datanucleus.query.flushBeforeExecution","true");
		return rv;
	}
	
	public Map<String,Object> getQueryObjects(){
		return this.queryObjects;
	}

	public void setBefore(Date before) {
		queryObjects.put(BFORE_PARAM, before);
	}

	public void setAfter(Date after) {
		queryObjects.put(AFTER_PARAM, after);
	}

//	TODO: implement an object query, something like that
//	public <K extends Model> Query createObjectQuery(
//			Map<String, String> queryParams, Integer offset, Integer limit,
//			Class<K> entityClass, Model m, Class<? extends Model> clazz) {
//		getPersistenceManager();
//		offset = (null == offset) ? 0 : offset;
//		limit = (null == limit) ? 0 : limit;
//		Query q = pm.newQuery(entityClass);
//		String filter = "id >=" + offset;
//		if (null != queryParams)
//			for (Entry<String, String> e : queryParams.entrySet()) {
//				// TODO: check for String sanity
//				if (e.getKey().equalsIgnoreCase(OBJECT_QUERY_PARAM)) {
//					if (m != null) {
//						q.declareParameters(clazz.getSimpleName() + " objectO");
//						q.declareImports("import " + clazz.getName() + ";");
//						filter = e.getValue() + " == objectO && " + filter;
//						q.getFetchPlan().setGroup(e.getValue());
//					} else {
//						throw new ParameterMissingException(
//								"no object for object query provided");
//					}
//				} else {
//					filter = e.getKey() + " == \"" + e.getValue() + "\" && "
//							+ filter;
//				}
//			}
//		q.setFilter(filter);
//		q.setOrdering("id asc");
//		q.getFetchPlan().setFetchSize((int) (limit + 1));
//		return q;
//	}

}
