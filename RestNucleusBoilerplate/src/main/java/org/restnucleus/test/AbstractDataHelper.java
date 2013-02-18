package org.restnucleus.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * integration tests should extend this class to have a local server for query testing
 * @author johba
 */
public abstract class AbstractDataHelper {
	public static int REST_PORT = 8182;
	public final static String REST_PATH = "/rest";
	public final static String REST_HOST = "localhost";
	public String restUrl = null;

	public static Component component = null;
	
	public static GenericRepository gr = null;

	abstract public JaxRsApplication getApp(Context c);

	abstract public Map<Class<? extends Model>, List<? extends Model>> getData();

	@Before
	public void create() throws Exception {
		if (null == AbstractDataHelper.component) {
			gr = new GenericRepository();
			component = new Component();
			Random generator = new Random();
			int randomIndex = generator.nextInt(100);
			REST_PORT += randomIndex;
			component.getServers().add(Protocol.HTTP, REST_PORT);
			// create JAX-RS runtime environment
			component.getDefaultHost().attach(REST_PATH,
					getApp(component.getContext().createChildContext()));
			try {
				component.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// add some data
			persist(getData());
		}
		restUrl = "http://" + REST_HOST + ":" + REST_PORT + REST_PATH;
	}

	@AfterClass
	public static void down() {
		if (null != component) {
			try {
				component.stop();
				component = null;
				System.out.println("Server stopped.");
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				gr.closePersistenceManager();
				System.out.println("Persistence Manager stopped.");
			}
		}
	}

	protected ObjectMapper om = new ObjectMapper();

	public String json(Object o) {
		try {
			return om.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private <E extends Model> void persist(
			Map<Class<? extends Model>, List<? extends Model>> data) {
		if (null != data) {
			for (Entry<Class<? extends Model>, List<? extends Model>> e : data.entrySet()) {
				try {
					if (null == e.getValue() || e.getValue().size() < 1) {
						System.out.println("no data found for: "
								+ e.getKey().getSimpleName());
						return;
					}
					for (Model m : e.getValue()){
						gr.add(m);
					}
					System.out.println(e.getKey().getSimpleName()
							+ " populated with " + e.getValue().size()
							+ " entities");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
