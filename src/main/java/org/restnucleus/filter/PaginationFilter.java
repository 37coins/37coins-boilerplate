package org.restnucleus.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.restnucleus.dao.RNQuery;

import com.google.inject.Singleton;

/**
 * sort and pagination.
 * 
 * @author johba
 */

@Singleton
public class PaginationFilter implements Filter {
	

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		//we generally only need to limit the result set on certain queries
		if (httpReq.getMethod().equalsIgnoreCase("GET") || httpReq.getMethod().equalsIgnoreCase("DELETE")){
			RNQuery q = null;
			if (httpReq.getAttribute(RNQuery.QUERY_PARAM)!=null){
				q = (RNQuery)httpReq.getAttribute(RNQuery.QUERY_PARAM);
			}else{
				q = new RNQuery();
				httpReq.setAttribute(RNQuery.QUERY_PARAM,q);
			}
			Map<String,String[]> form = httpReq.getParameterMap();
			//handle pagination
			// like proposed here: http://www.baeldung.com/2012/01/18/rest-pagination-in-spring/#httpheaders
			// advantage of following HATEOAS in contrast to range header pagination
			Long page = null;
			if (null!=form.get(RNQuery.PAGE))
				page = Long.parseLong(form.get(RNQuery.PAGE)[0]);
			Long size = null;
			if (null!=form.get(RNQuery.SIZE))
				size = Long.parseLong(form.get(RNQuery.SIZE)[0]);
			q.setRange(page, size);
			//handle ordering attribute
			//according to Todd Fredrich in "RESTful Best Practices.pdf"
			String sort = (null!=form.get(RNQuery.SORT))?form.get(RNQuery.SORT)[0]:null;
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
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {	
	}
	
}
