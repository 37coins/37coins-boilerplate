package org.restnucleus;

import java.util.HashSet;
import java.util.Set;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restnucleus.inject.ContextFactory;
import org.restnucleus.inject.PersistenceModule;
import org.restnucleus.resources.ExampleResource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;

/**
 * A Restlet wrapper for JaxRs apps.
 * 
 * @author johba
 */
public class ExampleApplication {
	
	static{
		JaxrsApiReader.setFormatString("");
	}
	
	public static void main(String[] args) throws Exception {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8080);
		// create JAX-RS runtime environment
		Set<Class<?>> rrcs = new HashSet<Class<?>>();
		rrcs.add(ExampleResource.class);
		Injector injector = Guice.createInjector(new PersistenceModule(rrcs));
		ContextFactory cf = injector.getInstance(ContextFactory.class);
		JaxRsApplication  a = cf.create(component.getContext().createChildContext());
		component.getDefaultHost().attach("/",a);
		component.start();
		System.out.println("Press any key to exit:");
		System.in.read();
	}

}
