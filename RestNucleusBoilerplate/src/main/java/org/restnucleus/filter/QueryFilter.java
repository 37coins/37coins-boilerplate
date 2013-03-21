package org.restnucleus.filter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.routing.Filter;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.exceptions.ParameterMissingException;

import com.strategicgains.util.date.DateAdapter;

import cz.jirutka.rsql.parser.ParseException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.model.Expression;

public class QueryFilter extends Filter {
	
	public QueryFilter(Context context) {
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
			String filter = form.getFirstValue(RNQuery.FILTER);
			if (null!=filter){
				Expression e = null;
				try {
					e = RSQLParser.parse(filter);
				} catch (ParseException ex) {
					throw new ParameterMissingException("rsql query could not be parsed.");
				}
				if (null!=e){
					q.addExpression(e);
				}
			}
			//handle before and after filter
			String before = form.getFirstValue(RNQuery.BEFORE);
			if (null!=before){
				try {
					q.setBefore(new DateAdapter().parse(before));
				} catch (Exception e) {
					throw new ParameterMissingException("'before' param is not in ISO 8601 time point format.");
				}
			}
			String after = form.getFirstValue(RNQuery.AFTER);
			if (null!=after){
				try {
					q.setAfter(new DateAdapter().parse(after));
				} catch (Exception e) {
					throw new ParameterMissingException("'after' param is not in ISO 8601 time point format.");
				}
			}
		}
		return Filter.CONTINUE;
	}
	
}
