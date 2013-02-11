package net.johannbarbie.persistance;

import static com.jayway.restassured.RestAssured.given;

import org.junit.Test;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.johannbarbie.persistance.stub.ExampleApplication;
import com.johannbarbie.persistance.stub.ExampleEntityResource;
import com.johannbarbie.persistance.test.AbstractDataHelper;

public class JaxRsTest extends AbstractDataHelper {
		
		@Override
		public JaxRsApplication getApp(Context c) {
	        return new ExampleApplication(c);
		}
	@Test
	public void test() {
		Response r = given().contentType(ContentType.JSON)
		.expect()
		.statusCode(404)
		.when()
		.get(restUrl + ExampleEntityResource.PATH + "/1");
		System.out.println(r.getBody().asString());
	}

}
