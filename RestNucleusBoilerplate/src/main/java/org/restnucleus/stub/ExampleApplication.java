package org.restnucleus.stub;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.engine.header.Header;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.routing.Router;
import org.restlet.util.Series;
import org.restnucleus.exceptions.ExceptionHandler;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.filter.OriginFilter;
import org.restnucleus.filter.SearchFilter;

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

	/*
	 * register your Filters here.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.ext.jaxrs.JaxRsApplication#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot() {

		ApplicationFilter pmc = new ApplicationFilter(getContext());
		SearchFilter ff = new SearchFilter(getContext());
		OriginFilter of = new OriginFilter(getContext());

		Router router = new Router(getContext());
		router.attachDefault(super.createInboundRoot());

		pmc.setNext(router);
		ff.setNext(pmc);
		of.setNext(ff);

		return of;
	}

}
