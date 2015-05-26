package org.resnucleus.test;

import org.junit.Assert;
import org.junit.Test;
import org.restnucleus.test.EmbeddedJetty;

public class EmbeddedJettyTest {

    @Test
    public void injector() throws Exception {
        EmbeddedJetty embeddedJetty = new EmbeddedJetty()
                .httpPort(0)
                .sslPort(0)
                .setWebApp("src/test/webapp")
                .keyStoreFile("src/test/webapp/WEB-INF/jetty-localhost.jks")
                .keyStorePass("jetty");
        try {
            embeddedJetty.start();
            Assert.assertNotNull(embeddedJetty.getInjector());
            Assert.assertTrue(embeddedJetty.getBaseUri(false).getPort() > 0);
            Assert.assertTrue(embeddedJetty.getBaseUri(true).getPort() > 0);
        } finally {
            embeddedJetty.stop();
        }
    }
}
