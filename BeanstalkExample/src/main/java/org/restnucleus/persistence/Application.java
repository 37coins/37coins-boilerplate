package org.restnucleus.persistence;

import java.util.HashSet;
import java.util.Set;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.routing.Router;
import org.restnucleus.exceptions.ExceptionHandler;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.filter.SearchFilter;
import org.restnucleus.stub.ExampleCollectionResource;
import org.restnucleus.stub.ExampleEntityResource;

public class Application extends JaxRsApplication {
	public static javax.ws.rs.core.Application app = null;
	
	public Application(Context context) {
		super(context);
		app = new javax.ws.rs.core.Application() {
			public Set<Class<?>> getClasses() {
				Set<Class<?>> rrcs = new HashSet<Class<?>>();
				rrcs.add(ExampleEntityResource.class);
				rrcs.add(ExampleCollectionResource.class);
				rrcs.add(ExceptionHandler.class);
				rrcs.add(ApiListingResource.class);
				return rrcs;
			}

		};
		this.add(app);
		// this.setGuard(...); // if needed
		// this.setRoleChecker(...); // if needed
	}

	
	@Override
	public Restlet createInboundRoot(){
		ApplicationFilter pmc = new ApplicationFilter(getContext());
		SearchFilter ff = new SearchFilter(getContext());
		
		Router router = new Router(getContext());
		router.attachDefault(super.createInboundRoot());
		
		pmc.setNext(router);
		ff.setNext(pmc);
		
		return ff;
	}
}
