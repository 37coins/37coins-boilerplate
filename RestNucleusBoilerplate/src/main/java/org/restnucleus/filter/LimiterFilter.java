package org.restnucleus.filter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.routing.Filter;
import org.restnucleus.dao.RNQuery;

/**
 * Limiting result set of get queries based on filter, sort and pagination.
 * 
 * @author johba
 */

public class LimiterFilter extends Filter {
	public static final String QUERY_PARAM = "org.restnucleus.Query";
	public static final String FILTER_PARAM = "filter";
	public static final String SORT_PARAM = "sort";
	public static final String PAGE_PARAM = "page";
	public static final String SIZE_PARAM = "size";
	
	public LimiterFilter(Context context) {
		super(context);
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		//we generally only need to limit the result set on certain queries
		if (request.getMethod() == Method.GET || request.getMethod() == Method.DELETE){
			RNQuery q = new RNQuery();
			request.getAttributes().put(QUERY_PARAM,q);
			Form form = request.getResourceRef().getQueryAsForm();
			//handle pagination
			// like proposed here: http://www.baeldung.com/2012/01/18/rest-pagination-in-spring/#httpheaders
			// advantage of following HATEOAS in contrast to range header pagination
			Long page = null;
			if (null!=form.getFirstValue(PAGE_PARAM))
				page = Long.parseLong(form.getFirstValue(PAGE_PARAM));
			Long size = null;
			if (null!=form.getFirstValue(SIZE_PARAM))
				size = Long.parseLong(form.getFirstValue(SIZE_PARAM));
			q.setRange(page, size);
			//handle filter attribute
			//according to Todd Fredrich in "RESTful Best Practices.pdf"
			String filter = form.getFirstValue(FILTER_PARAM);
			if (null!=filter){
				String[] a = filter.split("\\|");
				for (String s : a){
					String[] b = s.split("::");
					q.addFilter(b[0],b[1]);
				}
			}
			//handle ordering attribute
			//according to Todd Fredrich in "RESTful Best Practices.pdf"
			String sort = form.getFirstValue(SORT_PARAM);
			if (null!=sort){
				StringBuffer sb = new StringBuffer();
				String[] a = filter.split("\\|");
				for (String s : a){
					if (sb.length() > 1)
						sb.append(", ");
					if (s.charAt(0)=='-'){
						sb.append(s.substring(1, s.length()));
						sb.append(" desc");
					}else{
						sb.append(s);
						sb.append(" asc");
					}
				}
				q.setOrdering(sb.toString());
			}
		}
		return Filter.CONTINUE;
	}	
	
}
