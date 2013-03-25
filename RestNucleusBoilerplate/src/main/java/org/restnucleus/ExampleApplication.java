package org.restnucleus;

import java.util.HashSet;
import java.util.Set;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.InstantiateException;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.ext.jaxrs.ObjectFactory;
import org.restlet.routing.Router;
import org.restnucleus.exceptions.ExceptionHandler;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.filter.OriginFilter;
import org.restnucleus.filter.QueryFilter;
import org.restnucleus.filter.PaginationFilter;
import org.restnucleus.inject.PersistenceModule;
import org.restnucleus.resources.ApiListingResource;
import org.restnucleus.resources.ExampleResource;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A Restlet wrapper for JaxRs apps.
 * 
 * @author johba
 */
public class ExampleApplication extends JaxRsApplication {
	public static javax.ws.rs.core.Application app = null;
	public Injector injector = null;

	public ExampleApplication(Context context) {
		super(context);
		app = new javax.ws.rs.core.Application() {
			public Set<Class<?>> getClasses() {
				Set<Class<?>> rrcs = new HashSet<Class<?>>();
				rrcs.add(ExampleResource.class);
				rrcs.add(ExceptionHandler.class);
				rrcs.add(ApiListingResource.class);
				return rrcs;
			}

		};
		this.add(app);

		injector = Guice.createInjector(new PersistenceModule());

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
