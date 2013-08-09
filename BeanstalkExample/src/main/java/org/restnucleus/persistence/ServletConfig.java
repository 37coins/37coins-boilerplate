package org.restnucleus.persistence;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
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
import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;


public class ServletConfig extends GuiceServletContextListener {
	static {
		JaxrsApiReader.setFormatString("");
	}
	
	Logger log = LoggerFactory.getLogger(ServletConfig.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		PersistenceConfiguration.getInstance().getEntityManagerFactory();
		log.info("ServletContextListener started");
	}
	
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
    
    public void deregisterJdbc(){
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log.info(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
            	log.info(String.format("Error deregistering driver %s", driver));
                e.printStackTrace();
            }
        }
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            log.warn("SEVERE problem cleaning up: " + e.getMessage());
            e.printStackTrace();
        }    	
    }

    @Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
		PersistenceConfiguration.getInstance().closeEntityManagerFactory();
		deregisterJdbc();
		log.info("ServletContextListener destroyed");
	}

}
