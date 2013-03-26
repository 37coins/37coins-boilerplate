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
		Injector injector = Guice.createInjector(new PersistenceModule() {
			@Override
			public Set<Class<?>> getClassList() {
				Set<Class<?>> cs = new HashSet<>();
				cs.add(ExampleResource.class);
				return cs;
			}
		});
		ContextFactory cf = injector.getInstance(ContextFactory.class);
		JaxRsApplication  a = cf.create(component.getContext().createChildContext());
		component.getDefaultHost().attach("/",a);
		component.start();
		System.in.read();
	}
}
