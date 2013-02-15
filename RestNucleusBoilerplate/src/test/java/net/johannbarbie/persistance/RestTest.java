package net.johannbarbie.persistance;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;

import com.jayway.restassured.http.ContentType;
import com.johannbarbie.persistance.stub.Example;
import com.johannbarbie.persistance.stub.ExampleApplication;
import com.johannbarbie.persistance.stub.ExampleCollectionResource;
import com.johannbarbie.persistance.stub.ExampleEntityResource;
import com.johannbarbie.persistance.test.AbstractDataHelper;

/**
 * 
 * first class citizen residency.
 * 
 * @author johba
 */
public class RestTest extends AbstractDataHelper {
	
	@Override
	public JaxRsApplication getApp(Context c) {
		return new ExampleApplication(c);
	}

	@Test
	public void testCRUD() throws Exception {
		// test post
		Example example = new Example().setEmail("test@johba.com");
		example.setId(Long.parseLong(given().body(json(example))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleCollectionResource.PATH)
				.asString()));
		// fire get successfully
		String rv = given()
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl + ExampleEntityResource.PATH_ENTITY,
						example.getId()).asString();
		Assert.assertTrue(json(example).equals(rv));

		// test put
		Example example2 = new Example().setEmail("test2@johba.com");
		example2.setId(Long.parseLong(given()
				.body(json(example2))
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.put(restUrl + ExampleEntityResource.PATH_ENTITY,
						example.getId()).asString()));
		// test get
		rv = given()
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl + ExampleEntityResource.PATH_ENTITY,
						example.getId()).asString();
		example.setEmail("test2@johba.com");
		Assert.assertTrue(json(example).equals(rv));
		// test delete
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(204)
				.when()
				.delete(restUrl + ExampleEntityResource.PATH_ENTITY,
						example.getId());
		// test get
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(404)
				.when()
				.get(restUrl + ExampleEntityResource.PATH_ENTITY,
						example.getId()).asString();
	}

	@Test
	public void testQuery() throws Exception {
		// create test data
		Example example1 = new Example().setEmail("test1@johba.com");
		example1.setId(Long.parseLong(given().body(json(example1))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleCollectionResource.PATH)
				.asString()));
		Example example2 = new Example().setEmail("test1@johba.com");
		example2.setId(Long.parseLong(given().body(json(example2))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleCollectionResource.PATH)
				.asString()));
		Example example3 = new Example().setEmail("test3@johba.com");
		example3.setId(Long.parseLong(given().body(json(example3))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleCollectionResource.PATH)
				.asString()));
		Example example4 = new Example().setEmail("test4@johba.com");
		example4.setId(Long.parseLong(given().body(json(example4))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleCollectionResource.PATH)
				.asString()));
		List<Example> list = new ArrayList<Example>();
		list.add(example1);
		list.add(example2);
		list.add(example3);
		list.add(example4);
		// no paging, no parameters
		String rv = given().contentType(ContentType.JSON).expect()
				.statusCode(200).when()
				.get(restUrl + ExampleCollectionResource.PATH)
				.asString();
		Assert.assertEquals(rv, json(list));
		// paging, no paramters
		rv = given()
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl
						+ ExampleCollectionResource.PATH_PAGINATION, 2,
						1).asString();
		Assert.assertTrue(rv.contains(json(example2)));
		// wrong paging
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(400)
				.when()
				.get(restUrl
						+ ExampleCollectionResource.PATH_PAGINATION, 2,
						-1);
		// paging, one paramter
		rv = given()
				.contentType(ContentType.JSON)
				.param("email", "test1@johba.com")
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl
						+ ExampleCollectionResource.PATH_PAGINATION, 2,
						1).asString();
		list.clear();
		list.add(example2);
		Assert.assertEquals(rv, json(list));
		//TODO: paging, two parameter

		//TODO: no paging, but parameter
		
		//TODO: injection attack, to check query sanity
	}
	
	@Test
	public void testObjectQuery() throws Exception {
		Example example1 = new Example().setEmail("test1@johba.com");
		example1.setId(Long.parseLong(
				given()
					.body(json(example1))
					.contentType(ContentType.JSON)
				.expect()
					.statusCode(200)
				.when()
					.post(restUrl + ExampleCollectionResource.PATH).asString()));
		Example example2 = new Example().setEmail("test2@johba.com").setChild(example1);
		example2.setId(Long.parseLong(given().body(json(example2))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleCollectionResource.PATH)
				.asString()));
		String rv = 
				given()
					.body(json(example1))
					.contentType(ContentType.JSON)
				.expect()
					.statusCode(200)
				.when()
					.put(restUrl + ExampleCollectionResource.PATH).asString();
		Assert.assertEquals("["+json(example2)+"]", rv);
	}

}
