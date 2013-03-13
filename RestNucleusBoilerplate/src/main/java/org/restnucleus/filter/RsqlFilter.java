package org.restnucleus.filter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.routing.Filter;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.exceptions.ParameterMissingException;

import cz.jirutka.rsql.parser.ParseException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.model.Expression;

public class RsqlFilter extends Filter {
	public static final String RSQ = "rsq"; 
	
	public RsqlFilter(Context context) {
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
			String rsq = form.getFirstValue(RSQ);
			if (null!=rsq){
				Expression e = null;
				try {
					e = RSQLParser.parse(rsq);
				} catch (ParseException ex) {
					throw new ParameterMissingException("rsql query could not be parsed.");
				}
				if (null!=e){
					q.addExpression(e);
				}
			}
		}
		return Filter.CONTINUE;
	}
	
}
