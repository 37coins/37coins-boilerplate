package org.restnucleus.filter;

import java.text.ParseException;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.routing.Filter;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.exceptions.ParameterMissingException;

import com.strategicgains.util.date.DateAdapter;

/**
 * Limiting result set of get queries based on filter, sort and pagination.
 * 
 * @author johba
 */

public class SearchFilter extends Filter {
	
	public SearchFilter(Context context) {
		super(context);
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		//we generally only need to limit the result set on certain queries
		if (request.getMethod() == Method.GET || request.getMethod() == Method.DELETE){
			RNQuery q = new RNQuery();
			request.getAttributes().put(RNQuery.QUERY_PARAM,q);
			Form form = request.getResourceRef().getQueryAsForm();
			//handle pagination
			// like proposed here: http://www.baeldung.com/2012/01/18/rest-pagination-in-spring/#httpheaders
			// advantage of following HATEOAS in contrast to range header pagination
			Long page = null;
			if (null!=form.getFirstValue(RNQuery.PAGE_NAME))
				page = Long.parseLong(form.getFirstValue(RNQuery.PAGE_NAME));
			Long size = null;
			if (null!=form.getFirstValue(RNQuery.SIZE_NAME))
				size = Long.parseLong(form.getFirstValue(RNQuery.SIZE_NAME));
			q.setRange(page, size);
			//handle filter attribute
			//according to Todd Fredrich in "RESTful Best Practices.pdf"
			String filter = form.getFirstValue(RNQuery.FILTER_NAME);
			if (null!=filter){
				String[] a = filter.split("\\|");
				for (String s : a){
					String[] b = s.split("::");
					q.addFilter(b[0],b[1]);
				}
			}
			//handle ordering attribute
			//according to Todd Fredrich in "RESTful Best Practices.pdf"
			String sort = form.getFirstValue(RNQuery.SORT_NAME);
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
			//handle before and after filter
			String before = form.getFirstValue(RNQuery.BFORE_NAME);
			if (null!=before){
				try {
					q.setBefore(new DateAdapter().parse(before));
				} catch (ParseException e) {
					throw new ParameterMissingException("'before' param is not in ISO 8601 time point format.");
				}
			}
			String after = form.getFirstValue(RNQuery.AFTER_NAME);
			if (null!=after){
				try {
					q.setAfter(new DateAdapter().parse(after));
				} catch (ParseException e) {
					throw new ParameterMissingException("'after' param is not in ISO 8601 time point format.");
				}
			}
		}
		return Filter.CONTINUE;
	}	
	
}
