package org.restnucleus.stub;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.routing.Router;
import org.restnucleus.exceptions.ExceptionHandler;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.filter.LimiterFilter;

/**
 * A Restlet wrapper for JaxRs apps. 
 * 
 * @author johba
 */
public class ExampleApplication extends JaxRsApplication {

	/*
	 * register your Resource classes here.
	 */
	public ExampleApplication(Context context) {
		super(context);
		this.add(new Application() {
			public Set<Class<?>> getClasses() {
				Set<Class<?>> rrcs = new HashSet<Class<?>>();
				rrcs.add(ExampleEntityResource.class);
				rrcs.add(ExampleCollectionResource.class);
				rrcs.add(ExceptionHandler.class);
				return rrcs;
			}

		});
		// this.setGuard(...); // if needed
		// this.setRoleChecker(...); // if needed
	}
	
//	  @Override
//	  public void handle(Request request, Response response) {
//	    super.handle(request, response);
//	    Form responseHeaders = (Form)response.getAttributes().get("org.restlet.http.headers");
//	    if (responseHeaders == null) {
//	      response.getAttributes().put("org.restlet.http.headers", responseHeaders = new Form());
//	    }
//	    responseHeaders.add("Access-Control-Allow-Origin", "*");
//	    responseHeaders.add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
//	    responseHeaders.add("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
//	  }

	/*
	 * register your Filters here.
	 * 
	 * (non-Javadoc)
	 * @see org.restlet.ext.jaxrs.JaxRsApplication#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot(){
		
		ApplicationFilter pmc = new ApplicationFilter(getContext());
		LimiterFilter ff = new LimiterFilter(getContext());
		
		Router router = new Router(getContext());
		router.attachDefault(super.createInboundRoot());
		
		pmc.setNext(router);
		ff.setNext(pmc);
		
		return ff;
	}

}
