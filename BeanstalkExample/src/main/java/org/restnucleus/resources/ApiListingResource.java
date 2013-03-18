package org.restnucleus.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.restnucleus.persistence.ExampleApplication;

import com.typesafe.config.ConfigFactory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.Config;
import com.wordnik.swagger.jaxrs.listing.ApiListing;

@Path("/api-docs")
@Api("/api-docs")
@Produces({"application/json"})
public class ApiListingResource extends ApiListing {
	
	
	@Override
	public javax.ws.rs.core.Application getApp(){
		return ExampleApplication.app;
	}

	@Override
	public Config getConfig(){
		com.typesafe.config.Config conf = ConfigFactory.load();
		return new Config()
			.setApiVersion(conf.getString("api.version"))
			.setSwaggerVersion(conf.getString("swagger.version"))
			.setPackageList(conf.getString("api.model.packages"))
			.setBasePath(conf.getString("swagger.api.basepath"));
	}
	
}
