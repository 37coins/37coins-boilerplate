package org.restnucleus.test;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.restnucleus.dao.GenericRepository;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.DispatcherType;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;

public class EmbeddedJetty {

    private Server server;

    public static final int HTTP_PORT = 8087;
    public static final int HTTPS_PORT = 8088;

    private GenericRepository dao;
    
    public String setInitParam(ServletHolder holder){
    	holder.setInitParameter("javax.ws.rs.Application", "org.restnucleus.RestNucleusApplication");
    	return "src/main/webapp";
    }

    public void start() throws Exception {

        server = new Server();

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);

        bb.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        ServletHolder holder = bb.addServlet(ServletContainer.class, "/test/*");

        bb.addServlet(holder, "/bla/*");
        bb.setContextPath("/");
        bb.setWar(setInitParam(holder));

        server.setHandler(bb);

        // HTTP configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(HTTPS_PORT);
        http_config.setOutputBufferSize(32786);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);

        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(HTTP_PORT);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory(true);
        URL keystore = EmbeddedJetty.class.getClassLoader().getResource("jetty-ssl.keystore");
        sslContextFactory.setKeyStorePath(keystore.getPath());
        sslContextFactory.setKeyStorePassword("jetty6");
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
                new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        sslConnector.setPort(HTTPS_PORT);
        server.addConnector(sslConnector);

        System.out.println(">>> STARTING EMBEDDED JETTY SERVER");
        server.start();
        
        Injector injector = (Injector) bb.getServletContext().getAttribute(Injector.class.getName());
        dao = new GenericRepository(injector.getInstance(PersistenceManagerFactory.class));
    }
    
    public void stop() throws Exception{
        server.stop();
    }
    
    public GenericRepository getDao(){
    	return dao;
    }
    
    public URI getBaseUri(){
        return getBaseUri(false);
    }

    public URI getBaseUri(boolean secure) {
        String url = secure
                ? "https://localhost:" + HTTPS_PORT
                : "http://localhost:" + HTTP_PORT;
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }

    }

    public static void main(String[] args) {
        try {
            EmbeddedJetty j = new EmbeddedJetty();
            j.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}