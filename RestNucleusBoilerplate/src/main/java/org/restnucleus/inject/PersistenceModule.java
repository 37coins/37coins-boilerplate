package org.restnucleus.inject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.restlet.Request;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.exceptions.ExceptionHandler;
import org.restnucleus.filter.ApplicationFilter;
import org.restnucleus.resources.ApiListingResource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class PersistenceModule extends AbstractModule {
	
	public Set<Class<?>> getClassList(){
		return Collections.emptySet();
	}
	
	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
	     .implement(JaxRsApplication.class, org.restnucleus.Application.class)
	     .build(ContextFactory.class));
	}
	
	@Provides 
	GenericRepository provideDao() {
		return (GenericRepository) Request.getCurrent().getAttributes()
				.get(ApplicationFilter.DAO_PARAM);
		//we don't capture a null pointer here
		//creating new repo would make closing db connection unmanagebale
	}
	
	@Provides
	RNQuery getQuery(){
		RNQuery rv = (RNQuery) Request.getCurrent().getAttributes().get(RNQuery.QUERY_PARAM);
		if (null==rv)
			rv = new RNQuery();
		return rv;
	}
	
	@Provides
	Application getJaxRsApp(){
		Application app = new Application() {
			public Set<Class<?>> getClasses() {
				Set<Class<?>> classes = new HashSet<>();
				classes.add(ExceptionHandler.class);
				classes.add(ApiListingResource.class);
				Set<Class<?>> cs = getClassList();
				for (Class<?> c : cs)
					classes.add(c);

				return classes;
			}
		};
		return app;
	}
	
	

}
