package org.resnucleus.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import org.restnucleus.AbstractServletConfig;

public class SomeConfig extends AbstractServletConfig {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
        });
    }
}
