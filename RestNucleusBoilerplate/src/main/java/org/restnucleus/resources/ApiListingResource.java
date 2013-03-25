package org.restnucleus.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListing;

@Path("/api-docs")
@Api("/api-docs")
@Produces({"application/json"})
public class ApiListingResource extends ApiListing {
	
	@Inject
	private Application app;
	
	@Override
	public Application getApp(){
		return app;
	}
}
