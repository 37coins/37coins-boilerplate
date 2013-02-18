package org.restnucleus;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restnucleus.dao.Model;
import org.restnucleus.filter.LimiterFilter;
import org.restnucleus.stub.Example;
import org.restnucleus.stub.ExampleApplication;
import org.restnucleus.stub.ExampleCollectionResource;
import org.restnucleus.test.AbstractDataHelper;

import com.jayway.restassured.http.ContentType;

/**
 * 
 * integration test for collection resource
 * 
 * @author johba
 */
public class CollectionResourceTest extends AbstractDataHelper {
	public static List<Example> list = null;

	@Override
	public Map<Class<? extends Model>, List<? extends Model>> getData() {
		List<Example> rv = new ArrayList<Example>();
		rv.add(new Example().setEmail("test0@jb.com"));
		rv.add(new Example().setEmail("test1@jb.com"));
		rv.add(new Example().setEmail("test2@jb.com").setChild(
				(Example) rv.get(0)));
		Map<Class<? extends Model>, List<? extends Model>> data = new HashMap<Class<? extends Model>, List<? extends Model>>();
		data.put(Example.class, rv);
		CollectionResourceTest.list = rv;
		return data;
	}

	@Override
	public JaxRsApplication getApp(Context c) {
		return new ExampleApplication(c);
	}

	@Test
	public void testQuery() throws Exception {
		// no paging, no parameters
		String rv = given().contentType(ContentType.JSON).expect()
				.statusCode(200).when()
				.get(restUrl + ExampleCollectionResource.PATH).asString();
		Assert.assertEquals(rv, json(CollectionResourceTest.list));
		// paging, no paramters
		rv = given().contentType(ContentType.JSON)
				.header(LimiterFilter.PAGE_PARAM, "2").and()
				.header(LimiterFilter.SIZE_PARAM, "1").expect().statusCode(200)
				.when().get(restUrl + ExampleCollectionResource.PATH)
				.asString();
		Assert.assertTrue(rv.contains(json(CollectionResourceTest.list.get(2))));
		// // wrong paging
		// given().contentType(ContentType.JSON)
		// .expect()
		// .statusCode(400)
		// .when()
		// .get(restUrl
		// + ExampleCollectionResource.PATH_PAGINATION, 2,
		// -1);
		// paging, one paramter
		rv = given()
				.contentType(ContentType.JSON)
				.param("filter",
						"email::"
								+ (CollectionResourceTest.list.get(2))
										.getEmail()).expect().statusCode(200)
				.when().get(restUrl + ExampleCollectionResource.PATH)
				.asString();
		Assert.assertEquals(rv, "["+json(CollectionResourceTest.list.get(2))+"]");
		// TODO: paging, two parameter

		// TODO: no paging, but parameter

		// TODO: injection attack, to check query sanity
		
		//delete query
		given()
		.contentType(ContentType.JSON)
		.param("filter",
				"email::"
						+ (CollectionResourceTest.list.get(2))
								.getEmail()).expect().statusCode(204)
		.when().delete(restUrl + ExampleCollectionResource.PATH);
		rv = given()
		.contentType(ContentType.JSON)
		.param("filter",
				"email::"
						+ (CollectionResourceTest.list.get(2))
								.getEmail()).expect().statusCode(200)
		.when().get(restUrl + ExampleCollectionResource.PATH).asString();
		Assert.assertEquals("[]", rv);
	}
	// @Test
	// public void testObjectQuery() {
	// String rv = given().body(json(CollectionResourceTest.list.get(0)))
	// .contentType(ContentType.JSON).expect().statusCode(200).when()
	// .put(restUrl + ExampleCollectionResource.PATH).asString();
	// Assert.assertEquals("[" + json(CollectionResourceTest.list.get(2)) + "]",
	// rv);
	// }

}
