package org.restnucleus;

import javax.inject.Inject;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class RestNucleusApplication extends ResourceConfig {

    @Inject
    public RestNucleusApplication(ServiceLocator serviceLocator) {
        // Set package to look for resources in
        packages("org.restnucleus.resources","org.glassfish.jersey.examples.jackson");

        System.out.println("Registering injectables...");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(ServletConfig.injector);
        this.register(JacksonFeature.class);
    }
}