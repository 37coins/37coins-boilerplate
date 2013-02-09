package net.johannbarbie.persistance.stub;

import java.util.HashMap;
import java.util.Map;

import net.johannbarbie.persistance.resources.AbstractResource;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

public class ExampleApplication extends Application {
	
	
	public static final String REST_BASE = "";
	

	public static final String PATH_EXAMPLE_COLLECTION = "/example";
	public static final String PATH_EXAMPLE_COLLECTION_PAGING = PATH_EXAMPLE_COLLECTION+"/offset/{"+AbstractResource.OFFSET+"}/limit/{"+AbstractResource.LIMIT+"}";
	public static final String PATH_EXAMPLE_ONE = PATH_EXAMPLE_COLLECTION + "/{" + AbstractResource.ID + "}"; //


	public static class RouteTuple {
		private final String pathTemplate;
		private final Class<? extends ServerResource> cls;

		public RouteTuple(final String pathTemplate,
				final Class<? extends ServerResource> cls) {
			this.pathTemplate = pathTemplate;
			this.cls = cls;
		}

		public String getPathTemplate() {
			return pathTemplate;
		}

		public Class<? extends ServerResource> getCls() {
			return cls;
		}
	}
	

	public static Map<String, RouteTuple> SERVICES;
	

	
	private static void init() {
		SERVICES = new HashMap<String, RouteTuple>(); //	
		SERVICES.put("example.collection", new RouteTuple(PATH_EXAMPLE_COLLECTION, ExampleCollectionResource.class)); //
		SERVICES.put("example.collection.paging", new RouteTuple(PATH_EXAMPLE_COLLECTION_PAGING, ExampleCollectionResource.class)); //
		SERVICES.put("example.x", new RouteTuple(PATH_EXAMPLE_ONE, ExampleEntityResource.class)); //
	}
	
	public static String getEndpoint(final String key) {
		return REST_BASE+SERVICES.get(key).getPathTemplate();
	}

	@Override
    public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		ExampleApplication.init();

		getMetadataService().addExtension("json", MediaType.APPLICATION_JSON);
		getMetadataService().setDefaultMediaType(MediaType.APPLICATION_JSON);
		
		
		for (final RouteTuple tuple : SERVICES.values()) {
			router.attach(tuple.getPathTemplate(),tuple.getCls());
		}

		//return router;
		final OriginFilter originFilter = new OriginFilter(getContext());
		originFilter.setNext(router);
		return originFilter;
	}
}