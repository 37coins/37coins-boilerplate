package org.restnucleus.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.restnucleus.dao.GenericRepository;

import com.google.inject.Key;
import com.google.inject.name.Names;

@Singleton
public class PersistenceFilter implements Filter {

	private final GenericRepository dao;

	@Inject
	public PersistenceFilter(GenericRepository dao) {
		this.dao = dao;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		dao.getPersistenceManager();
		HttpServletRequest httpReq = (HttpServletRequest)request;
		httpReq.setAttribute(Key.get(GenericRepository.class, Names.named("dao")).toString(),dao);
		httpReq.setAttribute("gr", dao);
		try {
			chain.doFilter(request, response);
		} finally {
			dao.closePersistenceManager();
		}
	}

	@Override
	public void destroy() {
		dao.closePersistenceManager();
	}
}