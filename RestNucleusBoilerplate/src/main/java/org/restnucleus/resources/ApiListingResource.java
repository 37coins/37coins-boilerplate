package org.restnucleus.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.wordnik.swagger.jaxrs.listing.ApiListing;
import com.wordnik.swagger.annotations.Api;

@Path("/api-docs")
@Api("/api-docs")
@Produces({"application/json"})
public class ApiListingResource extends ApiListing {

}
