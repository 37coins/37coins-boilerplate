package org.restnucleus;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.inject.Singleton;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletContextEvent;

import org.restnucleus.filter.PaginationFilter;
import org.restnucleus.filter.PersistenceFilter;
import org.restnucleus.filter.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;


public class ServletConfig extends GuiceServletContextListener {
	
    public static Injector injector;
	
	Logger log = LoggerFactory.getLogger(ServletConfig.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		log.info("ServletContextListener started");
	}
	
    @Override
    protected Injector getInjector(){
    	System.out.println("Getting injector");
    	
        injector = Guice.createInjector(new ServletModule(){
            @Override
            protected void configureServlets(){
            	filter("/*").through(PersistenceFilter.class);
            	filter("/*").through(QueryFilter.class);
            	filter("/*").through(PaginationFilter.class);
            	super.configureServlets();
            }
			
			@Provides @Singleton @SuppressWarnings("unused")
			PersistenceManagerFactory providePersistence(){
				PersistenceConfiguration pc = new PersistenceConfiguration();
				pc.createEntityManagerFactory();
				return pc.getPersistenceManagerFactory();
			}
        });
        return injector;
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
		Injector injector = (Injector) sce.getServletContext().getAttribute(Injector.class.getName());
		injector.getInstance(PersistenceManagerFactory.class).close();
		deregisterJdbc();
		log.info("ServletContextListener destroyed");
		super.contextDestroyed(sce);
	}

}
