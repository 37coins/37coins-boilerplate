package org.restnucleus.persistence;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListing;
import com.wordnik.swagger.jaxrs.Config;

@Path("/api-docs")
@Api("/api-docs")
@Produces({"application/json"})
public class ApiListingResource extends ApiListing {
	
	
	@Override
	public javax.ws.rs.core.Application getApp(){
		return Application.app;
	}

	@Override
	public Config getConfig(){
		return new Config()
			.setApiVersion("1.0")
			.setSwaggerVersion("1.1")
			.setPackageList("org.restnucleus.persistence,org.restnucleus.persistence.stub,org.restnucleus.persistence.dao,org.restnucleus.persistence.resources")
			.setBasePath("http://localhost:8080/rest");
	}
	
}
