package com.johannbarbie.persistance.stub;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.routing.Router;

import com.johannbarbie.persistance.ApplicationFilter;
import com.johannbarbie.persistance.exceptions.ExceptionHandler;
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
	 * @see org.restlet.ext.jaxrs.JaxRsApplication#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot(){
		
		ApplicationFilter pmc = new ApplicationFilter(getContext());
		
		Router router = new Router(getContext());
		router.attachDefault(super.createInboundRoot());
		
		pmc.setNext(router);
		
		return pmc;
	}

}
