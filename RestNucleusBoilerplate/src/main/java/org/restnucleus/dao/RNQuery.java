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
	public static final String BEFORE = "before";
	public static final String BFORE_DESC = "query paramater for creationDate before x.";
	public static final String AFTER = "after";
	public static final String AFTER_DESC = "query paramater for creationDate after x.";
	public static final String QUERY_PARAM = "org.restnucleus.Query";
	public static final String FILTER = "filter";
	public static final String FILTER_DESC = "like param1::value1|param2::value2";
	public static final String SORT = "sort";
	public static final String SORT_DESC = "like param1|-param2  (- for ascending)";
	public static final String PAGE = "page";
	public static final String PAGE_DESC = "page number for pagination";
	public static final String SIZE = "size";
	public static final String SIZE_DESC = "page-size for pagination";
	
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
	
	public RNQuery clearFilter(){
		filter.clear();
		return this;
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
			StringBuffer filter = new StringBuffer();
			filter.append(this.getJdoFilter());
			StringBuffer params = new StringBuffer();
			StringBuffer imports = new StringBuffer();
			if (queryObjects.containsKey(BEFORE)){
				if (params.length()>3)
					params.append(", ");
				params.append("Date "+BEFORE);
				if (filter.length()>3)
					filter.append(" && ");
				filter.append("this.creationTime < ");
				filter.append(BEFORE);
				if (imports.length()>3)
					imports.append("; ");
				imports.append("import java.util.Date");
			}
			if (queryObjects.containsKey(AFTER)){
				if (params.length()>3)
					params.append(", ");
				params.append("Date "+AFTER);
				
				if (filter.length()>3)
					filter.append(" && ");
				filter.append("this.creationTime > ");
				filter.append(AFTER);			
				if (imports.length()>3)
					imports.append("; ");
				imports.append("import java.util.Date");
			}
			for (Entry<String,Object> e : queryObjects.entrySet()){
				if (e.getKey() != AFTER && e.getKey() != BEFORE){
					if (params.length()>3)
						params.append(", ");
					params.append(e.getValue().getClass().getSimpleName()+" "+e.getKey());
					
					if (filter.length()>3)
						filter.append(" && ");
					filter.append("this."+e.getKey()+" == "+e.getKey());
					if (imports.length()>3)
						imports.append("; ");
					imports.append("import "+e.getValue().getClass().getName());
				}
			}
			if (queryObjects.size() > 0){
				rv.declareImports(imports.toString());
				rv.setFilter(filter.toString());
				if (params.length()>3)
					rv.declareParameters(params.toString());
			}
		}
		// some optimizations
		rv.addExtension("datanucleus.query.flushBeforeExecution","true");
		return rv;
	}
	
	public Map<String,Object> getQueryObjects(){
		return this.queryObjects;
	}

	public RNQuery setBefore(Date before) {
		queryObjects.put(BEFORE, before);
		return this;
	}

	public RNQuery setAfter(Date after) {
		queryObjects.put(AFTER, after);
		return this;
	}
	
	public RNQuery addQueryObject(String name, Object value){
		queryObjects.put(name, value);
		return this;
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
