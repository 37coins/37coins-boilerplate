package org.restnucleus.filter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.routing.Filter;
import org.restnucleus.dao.RNQuery;

/**
 * sort and pagination.
 * 
 * @author johba
 */

public class PaginationFilter extends Filter {
	
	public PaginationFilter(Context context) {
		super(context);
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		//we generally only need to limit the result set on certain queries
		if (request.getMethod() == Method.GET || request.getMethod() == Method.DELETE){
			RNQuery q = null;
			if (request.getAttributes().containsKey(RNQuery.QUERY_PARAM)){
				q = (RNQuery)request.getAttributes().get(RNQuery.QUERY_PARAM);
			} else {
				q = new RNQuery();
				request.getAttributes().put(RNQuery.QUERY_PARAM,q);
			}
			Form form = request.getResourceRef().getQueryAsForm();
			//handle pagination
			// like proposed here: http://www.baeldung.com/2012/01/18/rest-pagination-in-spring/#httpheaders
			// advantage of following HATEOAS in contrast to range header pagination
			Long page = null;
			if (null!=form.getFirstValue(RNQuery.PAGE))
				page = Long.parseLong(form.getFirstValue(RNQuery.PAGE));
			Long size = null;
			if (null!=form.getFirstValue(RNQuery.SIZE))
				size = Long.parseLong(form.getFirstValue(RNQuery.SIZE));
			q.setRange(page, size);
			//handle ordering attribute
			//according to Todd Fredrich in "RESTful Best Practices.pdf"
			String sort = form.getFirstValue(RNQuery.SORT);
			if (null!=sort){
				StringBuffer sb = new StringBuffer();
				String[] a = sort.split("\\|");
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
