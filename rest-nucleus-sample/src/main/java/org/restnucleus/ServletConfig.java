package org.restnucleus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.filter.CorsFilter;
import org.restnucleus.filter.PaginationFilter;
import org.restnucleus.filter.QueryFilter;

import javax.inject.Singleton;
import javax.jdo.PersistenceManagerFactory;

public class ServletConfig extends AbstractServletConfig {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(QueryFilter.class);
                filter("/*").through(CorsFilter.class);
                filter("/*").through(PaginationFilter.class);
                super.configureServlets();
            }

            @Provides
            @Singleton
            @SuppressWarnings("unused")
            CorsFilter provideCors() {
                return new CorsFilter("*");
            }

            @Provides
            @RequestScoped
            @SuppressWarnings("unused")
            GenericRepository providePersistenceManager(PersistenceManagerFactory pmf) {
                GenericRepository dao = new GenericRepository(pmf);
                dao.getPersistenceManager();
                return dao;
            }

            @Provides
            @Singleton
            @SuppressWarnings("unused")
            PersistenceManagerFactory providePersistence() {
                PersistenceConfiguration pc = new PersistenceConfiguration();
                pc.createEntityManagerFactory();
                return pc.getPersistenceManagerFactory();
            }
        });
    }
}
