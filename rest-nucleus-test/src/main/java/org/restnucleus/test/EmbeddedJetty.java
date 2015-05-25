package org.restnucleus.test;

import com.google.inject.Injector;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerContainer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedJetty {

    private Server server;
    private Injector injector;

    private int httpPort = 8087;
    private int sslPort = 8088;
    private String webApp = "src/main/webapp";
    private String keyStoreFile;
    private String keyStorePass;
    private List<Class> webSocketEndpoints = new ArrayList<>();

    public void addWebSocketEndpoint(Class endpoint) {
        webSocketEndpoints.add(endpoint);
    }

    public EmbeddedJetty httpPort(int httpPort) {
        this.httpPort = httpPort;
        return this;
    }

    public EmbeddedJetty sslPort(int sslPort) {
        this.sslPort = sslPort;
        return this;
    }

    public EmbeddedJetty setWebApp(String webApp) {
        this.webApp = webApp;
        return this;
    }

    public EmbeddedJetty keyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
        return this;
    }

    public EmbeddedJetty keyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
        return this;
    }

    public void start() throws Exception {

        if (keyStoreFile == null) {
            throw new IllegalArgumentException("keyStoreFile required");
        }

        if (keyStorePass == null) {
            throw new IllegalArgumentException("keyStorePass required");
        }

        server = new Server();
        WebAppContext webAppContext = new WebAppContext(webApp, "/");
        webAppContext.setServer(server);
        server.setHandler(webAppContext);

        // HTTP configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        if (sslPort >= 0) {
            http_config.setSecurePort(sslPort);
        }
        http_config.setSendDateHeader(false);

        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        if (httpPort > 0) {
            http.setPort(httpPort);
        }
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory(true);
        sslContextFactory.setKeyStorePath(keyStoreFile);
        sslContextFactory.setKeyStorePassword(keyStorePass);
        sslContextFactory.setExcludeCipherSuites(
                "SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        // SSL HTTP Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        if (sslPort > 0) {
            sslConnector.setPort(sslPort);
        }
        server.addConnector(sslConnector);

        // Initialize javax.websocket layer
        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(webAppContext);
        for (Class webSocketEndpoint : webSocketEndpoints) {
            wscontainer.addEndpoint(webSocketEndpoint);
        }

        server.start();
        if (httpPort <= 0) {
            httpPort = http.getLocalPort();
        }
        if (sslPort <= 0) {
            sslPort = sslConnector.getLocalPort();
        }

        injector = (Injector) webAppContext.getServletContext().getAttribute(Injector.class.getName());
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
            injector = null;
        }
    }

    public Injector getInjector() {
        return injector;
    }

    public URI getBaseUri() {
        return getBaseUri(false);
    }

    public URI getBaseUri(boolean secure) {
        String url = secure
                ? "https://localhost:" + sslPort
                : "http://localhost:" + httpPort;
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}