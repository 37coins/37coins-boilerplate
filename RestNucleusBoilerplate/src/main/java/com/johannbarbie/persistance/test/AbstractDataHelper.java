package com.johannbarbie.persistance.test;

import java.io.IOException;
import java.util.Random;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;

public abstract class AbstractDataHelper {
	public static int REST_PORT = 8182;
	public final static String REST_PATH = "/rest";
	public final static String REST_HOST = "localhost";
	public String restUrl = null;

	public static Component component = null;

	abstract public JaxRsApplication getApp(Context c);

	@Before
	public void create() throws Exception {
		if (null == AbstractDataHelper.component) {
			component = new Component();
			 Random generator = new Random();
			 int randomIndex = generator.nextInt(100);
			 REST_PORT += randomIndex;
			component.getServers().add(Protocol.HTTP, REST_PORT);
	        // create JAX-RS runtime environment
			component.getDefaultHost().attach(REST_PATH, getApp(component.getContext()));
			try {
				component.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		restUrl = "http://" + REST_HOST + ":" + REST_PORT + REST_PATH;
	}

	@AfterClass
	public static void down() {
		if (null != component) {
			try {
				component.stop();
				component = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ObjectMapper om = new ObjectMapper();

	// abstract public void setup();
	//
	// abstract public void tearDown();

	public String json(Object o) {
		try {
			return om.writeValueAsString(o);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
