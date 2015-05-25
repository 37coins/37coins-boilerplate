package org.restnucleus;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.restnucleus.dao.Example;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.Model;
import org.restnucleus.resources.ExampleResource;
import org.restnucleus.test.DbHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jdo.PersistenceManagerFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.ContextResolver;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class JerseyExampleTest extends JerseyTest {

    static class InMemPersistenceManagerFactory implements Factory<PersistenceManagerFactory> {

        @Override
        public PersistenceManagerFactory provide() {
            DbHelper.clearInMemoryDb();
            PersistenceConfiguration pc = new PersistenceConfiguration();
            pc.createEntityManagerFactory();
            return pc.getPersistenceManagerFactory();
        }

        @Override
        public void dispose(PersistenceManagerFactory instance) {
            System.out.println("nobody care about dispose!");
            instance.close();
        }
    }

    static class InMemGenericRepository implements Factory<GenericRepository> {
        final PersistenceManagerFactory pmf;

        @Inject
        public InMemGenericRepository(PersistenceManagerFactory factory) {
            pmf = factory;
        }

        @Override
        public GenericRepository provide() {
            return new GenericRepository(pmf);
        }

        @Override
        public void dispose(GenericRepository instance) {
            System.out.println("nobody care about dispose!");
            instance.closePersistenceManager();
        }
    }

    static class QueryFactory implements Factory<RNQueryBean> {


        public QueryFactory() {

        }

        @Override
        public RNQueryBean provide() {
            return null;
        }

        @Override
        public void dispose(RNQueryBean instance) {

        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(ExampleResource.class)
                .register(IndentJsonProvider.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bindFactory(InMemPersistenceManagerFactory.class)
                                .to(PersistenceManagerFactory.class)
                                .in(Singleton.class);

                        bindFactory(InMemGenericRepository.class)
                                .to(GenericRepository.class);
                    }
                })
                .register(new ContextResolver<RNQueryBean>() {
                    @Override
                    public RNQueryBean getContext(Class<?> type) {
                        System.out.println("type:" + type);
                        return null;
                    }
                })
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        //bind(Query.class).in(Context.class);
                    }
                });
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JacksonJsonProvider.class);
    }


    @Test
    public void creation() {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(17);

        Model m1 = new Example()
                .setAmount(new BigDecimal("100.01"))
                .setEmail("some@some.com")
                .setCreationTime(utc.getTime());

        Model m2 = new Example()
                .setAmount(new BigDecimal("200.02"))
                .setEmail("other@point.com")
                .setCreationTime(utc.getTime());

        assertEquals(
                Long.valueOf(0),
                target("/example").request().buildPost(Entity.json(m1)).invoke(Long.class));

        assertEquals(
                Long.valueOf(1),
                target("/example").request().buildPost(Entity.json(m2)).invoke(Long.class));

        assertEquals(
                "[ {\n" +
                        "  \"id\" : 1,\n" +
                        "  \"creationTime\" : \"1970-01-01T00:00:00.017Z\",\n" +
                        "  \"email\" : \"other@point.com\",\n" +
                        "  \"amount\" : 200.02\n" +
                        "}, {\n" +
                        "  \"id\" : 0,\n" +
                        "  \"creationTime\" : \"1970-01-01T00:00:00.017Z\",\n" +
                        "  \"email\" : \"some@some.com\",\n" +
                        "  \"amount\" : 100.01\n" +
                        "} ]",
                target("/example")
                        .queryParam("size", 10)
                        .queryParam("sort", "id desc")
                        .request().get(String.class));
    }

    @Test
    public void year2015() {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(0);
        utc.set(Calendar.YEAR, 2015);

        Model m1 = new Example()
                .setAmount(new BigDecimal("111.001"))
                .setEmail("first@mail.com")
                .setCreationTime(utc.getTime());

        assertEquals(
                Long.valueOf(0),
                target("/example").request().buildPost(Entity.json(m1)).invoke(Long.class));

        assertEquals(
                "[ {\n" +
                        "  \"id\" : 0,\n" +
                        "  \"creationTime\" : \"2015-01-01T00:00:00.000Z\",\n" +
                        "  \"email\" : \"first@mail.com\",\n" +
                        "  \"amount\" : 111.001\n" +
                        "} ]",
                target("/example")
                        .queryParam("sort", "id desc")
                        .request().get(String.class));
    }
}
