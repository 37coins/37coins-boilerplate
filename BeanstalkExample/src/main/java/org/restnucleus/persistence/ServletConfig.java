package org.restnucleus.persistence;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;

import org.restnucleus.PersistenceConfiguration;
import org.restnucleus.inject.PersistenceModule;
import org.restnucleus.resources.ExampleResource;
import org.restnucleus.servlet.RestletServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;


public class ServletConfig extends GuiceServletContextListener {
	static {
		JaxrsApiReader.setFormatString("");
	}
	
	Logger log = LoggerFactory.getLogger(ServletConfig.class);
	
    @Override
    protected Injector getInjector(){
        return Guice.createInjector(new ServletModule(){
            @Override
            protected void configureServlets(){
                serve("/rest/*").with(RestletServlet.class);
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
		log.info("ServletContextListener destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		PersistenceConfiguration.getInstance().getEntityManagerFactory();
		log.info("ServletContextListener started");
	}

}
