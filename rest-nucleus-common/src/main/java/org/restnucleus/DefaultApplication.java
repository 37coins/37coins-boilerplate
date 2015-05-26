package org.restnucleus;

import org.glassfish.jersey.server.ResourceConfig;

public class DefaultApplication extends ResourceConfig {
    public DefaultApplication() {
        register(IndentJsonProvider.class);
    }
}
