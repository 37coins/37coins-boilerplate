package org.restnucleus;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.InstantiateException;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.ext.jaxrs.ObjectFactory;
import org.restlet.routing.Router;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.filter.OriginFilter;
import org.restnucleus.filter.PaginationFilter;
import org.restnucleus.filter.QueryFilter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

/**
 * A Restlet wrapper for JaxRs apps.
 * 
 * @author johba
 */
public class Application extends JaxRsApplication {

	@Inject
	public Application(javax.ws.rs.core.Application app,
			final Injector injector,
			@Assisted Context context) {
		super(context);
		this.add(app);
		
		this.setObjectFactory(new ObjectFactory() {
			@Override
			public <T> T getInstance(Class<T> jaxRsClass)
					throws InstantiateException {
				return injector.getInstance(jaxRsClass);
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
		PaginationFilter ff = new PaginationFilter(getContext());
		QueryFilter rf = new QueryFilter(getContext());
		OriginFilter of = new OriginFilter(getContext());

		Router router = new Router(getContext());
		router.attachDefault(super.createInboundRoot());

		pmc.setNext(router);
		ff.setNext(pmc);
		rf.setNext(ff);
		of.setNext(rf);

		return of;
	}

}
