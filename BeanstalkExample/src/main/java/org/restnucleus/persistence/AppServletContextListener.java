package org.restnucleus.persistence;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.restnucleus.PersistenceConfiguration;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;


public class AppServletContextListener implements ServletContextListener {
	static {
		JaxrsApiReader.setFormatString("");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		PersistenceConfiguration.getInstance().closeEntityManagerFactory();
		System.out.println("ServletContextListener destroyed");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		PersistenceConfiguration.getInstance().getEntityManagerFactory();
		System.out.println("ServletContextListener started");
	}

}