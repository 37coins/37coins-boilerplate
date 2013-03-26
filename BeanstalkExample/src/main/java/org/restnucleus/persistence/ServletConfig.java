package org.restnucleus.persistence;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;

import org.restnucleus.PersistenceConfiguration;
import org.restnucleus.inject.PersistenceModule;
import org.restnucleus.resources.ExampleResource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;


public class ServletConfig extends GuiceServletContextListener {
	static {
		JaxrsApiReader.setFormatString("");
	}
	
    @Override
    protected Injector getInjector(){
        return Guice.createInjector(new ServletModule(){
            @Override
            protected void configureServlets(){
                serve("/rest/*").with(CommunityServlet.class);
            }
        },new PersistenceModule(){
			@Override
			public Set<Class<?>> getClassList() {
				Set<Class<?>> cs = new HashSet<>();
				cs.add(ExampleResource.class);
				return cs;
			}
		});
    }

    @Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
		PersistenceConfiguration.getInstance().closeEntityManagerFactory();
		System.out.println("ServletContextListener destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		PersistenceConfiguration.getInstance().getEntityManagerFactory();
		System.out.println("ServletContextListener started");
	}

}
