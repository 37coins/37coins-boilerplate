package org.restnucleus.filter;

import com.strategicgains.util.date.DateAdapter;
import cz.jirutka.rsql.parser.ParseException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.model.Expression;
import org.restnucleus.RNQueryBean;
import org.restnucleus.dao.RNQuery;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.Map;

/**
 * @deprecated Use {@link RNQueryBean} and {@link BeanParam} instead.
 */
@Deprecated
@Singleton
public class QueryFilter implements Filter {

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        //we generally only need to limit the result set on certain queries
        if (httpReq.getMethod().equalsIgnoreCase("GET") || httpReq.getMethod().equalsIgnoreCase("DELETE")) {
            RNQuery q = null;
            if (httpReq.getAttribute(RNQuery.QUERY_PARAM) != null) {
                q = (RNQuery) httpReq.getAttribute(RNQuery.QUERY_PARAM);
            } else {
                q = new RNQuery();
                httpReq.setAttribute(RNQuery.QUERY_PARAM, q);
            }
            Map<String, String[]> form = httpReq.getParameterMap();
            String filter = (null != form.get(RNQuery.FILTER)) ? form.get(RNQuery.FILTER)[0] : null;
            if (null != filter) {
                Expression e = null;
                try {
                    e = RSQLParser.parse(filter);
                } catch (ParseException ex) {
                    throw new WebApplicationException(
                            "rsql query could not be parsed.",
                            javax.ws.rs.core.Response.Status.BAD_REQUEST);
                }
                if (null != e) {
                    q.addExpression(e);
                }
            }
            //handle before and after filter
            String before = (null != form.get(RNQuery.BEFORE)) ? form.get(RNQuery.BEFORE)[0] : null;
            if (null != before) {
                try {
                    q.setBefore(new DateAdapter().parse(before));
                } catch (Exception e) {
                    throw new WebApplicationException(
                            "'before' param is not in ISO 8601 time point format.",
                            javax.ws.rs.core.Response.Status.BAD_REQUEST);
                }
            }
            String after = (null != form.get(RNQuery.AFTER)) ? form.get(RNQuery.AFTER)[0] : null;
            if (null != after) {
                try {
                    q.setAfter(new DateAdapter().parse(after));
                } catch (Exception e) {
                    throw new WebApplicationException(
                            "'after' param is not in ISO 8601 time point format.",
                            javax.ws.rs.core.Response.Status.BAD_REQUEST);
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
