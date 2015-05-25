package org.restnucleus;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

abstract public class AbstractServletConfig extends ServletContainer {

    private GuiceServletContextListener injectorHolder = new GuiceServletContextListener() {
        @Override
        protected Injector getInjector() {
            return AbstractServletConfig.this.createInjector();
        }
    };

    /**
     * create new injector
     */
    protected abstract Injector createInjector();

    protected boolean skipDeRegisterFix;

    protected Logger log() {
        return LoggerFactory.getLogger(getClass());
    }

    @Override
    public void init() throws ServletException {
        injectorHolder.contextInitialized(new ServletContextEvent(getServletContext()));
        super.init();
        ServiceLocator serviceLocator = getApplicationHandler().getServiceLocator();
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        serviceLocator.getService(GuiceIntoHK2Bridge.class).bridgeGuiceInjector(injector());
        skipDeRegisterFix = Boolean.parseBoolean(getServletConfig().getInitParameter("skipDeRegisterFix"));
    }

    @Override
    public void destroy() {
        super.destroy();
        if (!skipDeRegisterFix) {
            Binding<PersistenceManagerFactory> binding = injector().getExistingBinding(Key.get(PersistenceManagerFactory.class));
            if (binding != null && Scopes.isSingleton(binding)) {
                PersistenceManagerFactory pmf = binding.getProvider().get();
                if (pmf != null && !pmf.isClosed()) {
                    pmf.close();
                }
            }
            injectorHolder.contextDestroyed(new ServletContextEvent(getServletContext()));
            deRegisterJdbc();
        }
    }

    protected Injector injector() {
        return (Injector) getServletContext().getAttribute(Injector.class.getName());
    }

    /**
     * This manually de-registers JDBC driver,
     * which prevents Tomcat 7 from complaining about memory leaks this class.
     */
    private void deRegisterJdbc() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log().info("deRegistering jdb driver: {}", driver);
            } catch (SQLException e) {
                log().info(String.format("Error deRegistering driver %s", driver), e);
            }
        }
        Class<?> cleanupThread = null;
        try {
            cleanupThread = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
        } catch (ClassNotFoundException ignore) {
        }
        if (cleanupThread != null) {
            try {
                Method shutdown = cleanupThread.getMethod("shutdown");
                shutdown.invoke(null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log().info("cleanup mysql connections", e);
            }
        }
    }

}
