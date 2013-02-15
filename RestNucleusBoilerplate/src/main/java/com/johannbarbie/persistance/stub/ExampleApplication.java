package com.johannbarbie.persistance.stub;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.routing.Router;

import com.johannbarbie.persistance.exceptions.ExceptionHandler;

public class ExampleApplication extends JaxRsApplication {

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

	
	@Override
	public Restlet createInboundRoot(){
		
		ApplicationFilter pmc = new ApplicationFilter(getContext());
		
		Router router = new Router(getContext());
		router.attachDefault(super.createInboundRoot());
		
		pmc.setNext(router);
		
		return pmc;
	}

}
