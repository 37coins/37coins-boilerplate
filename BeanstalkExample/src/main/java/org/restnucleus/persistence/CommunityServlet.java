package org.restnucleus.persistence;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.ext.servlet.ServletAdapter;
import org.restnucleus.inject.ContextFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class CommunityServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Inject
    private Injector injector;
    private Context context;
    private ServletAdapter adapter;


    @Override
    public void init() throws ServletException{
        context = new Context();
        ContextFactory cf = injector.getInstance(ContextFactory.class);
		Application  a = cf.create(context);
        adapter = new ServletAdapter(getServletContext());
        adapter.setNext(a);
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        adapter.service(request, response);
    }
}
