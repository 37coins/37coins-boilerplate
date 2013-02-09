package net.johannbarbie.persistance;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.johannbarbie.persistance.resources.AbstractAuthResource;
import net.johannbarbie.persistance.resources.AbstractResource;
import net.johannbarbie.persistance.stub.Example;
import net.johannbarbie.persistance.stub.ExampleApplication;
import net.johannbarbie.persistance.test.AbstractDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Application;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Cookies;

public class RestTest extends AbstractDataHelper {

	public static Cookies cookies = null;
	public static String cryptLoad = null;
	
	@Override
	public Application getApp() {
		return new ExampleApplication();
	}

	@Before
	public void create() throws Exception {
		super.create();
		cryptLoad = AbstractResource.ENCRYPT("bla");
		cookies = new Cookies(
				new Cookie.Builder(AbstractAuthResource.DEFAULT_SESSION,
						cryptLoad).build(),
				new Cookie.Builder(AbstractAuthResource.DEFAULT_USER, cryptLoad)
						.build());
	}

	@Test
	public void testCRUD() throws Exception {
		// test post
		Example example = new Example().setEmail("test@johannbarbie.net");
		example.setId(Long.parseLong(given().body(json(example))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		// get unauthorized when requesting an entity
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(401)
				.when()
				.get(restUrl + ExampleApplication.PATH_EXAMPLE_ONE,
						example.getId()).getStatusCode();
		// use authentication and fire get successfully
		String rv = given()
				.cookies(cookies)
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl + ExampleApplication.PATH_EXAMPLE_ONE,
						example.getId()).asString();
		Assert.assertTrue(json(example).equals(rv));

		// test put
		Example example2 = new Example().setEmail("test2@johannbarbie.net");
		example2.setId(Long.parseLong(given()
				.body(json(example2))
				.cookies(cookies)
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.put(restUrl + ExampleApplication.PATH_EXAMPLE_ONE,
						example.getId()).asString()));
		// test get, but authenticate with paramater
		rv = given()
				.contentType(ContentType.JSON)
				.parameter(AbstractAuthResource.DEFAULT_SESSION, cryptLoad)
				.parameter(AbstractAuthResource.DEFAULT_USER, cryptLoad)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl + ExampleApplication.PATH_EXAMPLE_ONE,
						example.getId()).asString();
		example.setEmail("test2@johannbarbie.net");
		Assert.assertTrue(json(example).equals(rv));
		// test delete
		given().contentType(ContentType.JSON)
				.cookies(cookies)
				.expect()
				.statusCode(200)
				.when()
				.delete(restUrl + ExampleApplication.PATH_EXAMPLE_ONE,
						example.getId());
		// test get
		given().contentType(ContentType.JSON)
				.cookies(cookies)
				.expect()
				.statusCode(404)
				.when()
				.get(restUrl + ExampleApplication.PATH_EXAMPLE_ONE,
						example.getId()).asString();
	}

	@Test
	public void testQuery() throws Exception {
		// create test data
		Example example1 = new Example().setEmail("test1@johannbarbie.net");
		example1.setId(Long.parseLong(given().body(json(example1))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		Example example2 = new Example().setEmail("test1@johannbarbie.net");
		example2.setId(Long.parseLong(given().body(json(example2))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		Example example3 = new Example().setEmail("test3@johannbarbie.net");
		example3.setId(Long.parseLong(given().body(json(example3))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		Example example4 = new Example().setEmail("test4@johannbarbie.net");
		example4.setId(Long.parseLong(given().body(json(example4))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		List<Example> list = new ArrayList<Example>();
		list.add(example1);
		list.add(example2);
		list.add(example3);
		list.add(example4);
		// no paging, no parameters
		String rv = given().contentType(ContentType.JSON).expect()
				.statusCode(200).when()
				.get(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString();
		Assert.assertEquals(rv, json(list));
		// paging, no paramters
		rv = given()
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl
						+ ExampleApplication.PATH_EXAMPLE_COLLECTION_PAGING, 2,
						1).asString();
		Assert.assertTrue(rv.contains(json(example2)));
		// wrong paging
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(400)
				.when()
				.get(restUrl
						+ ExampleApplication.PATH_EXAMPLE_COLLECTION_PAGING, 2,
						-1);
		// paging, one paramter
		rv = given()
				.contentType(ContentType.JSON)
				.param("email", "test1@johannbarbie.net")
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl
						+ ExampleApplication.PATH_EXAMPLE_COLLECTION_PAGING, 2,
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
		Example example1 = new Example().setEmail("test1@johannbarbie.net");
		example1.setId(Long.parseLong(given().body(json(example1))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		Example example2 = new Example().setEmail("test1@johannbarbie.net").setChild(example1);
		example2.setId(Long.parseLong(given().body(json(example2))
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString()));
		String rv = given().body(json(example1)).contentType(ContentType.JSON).expect()
				.statusCode(200).when()
				.put(restUrl + ExampleApplication.PATH_EXAMPLE_COLLECTION)
				.asString();
		Assert.assertEquals("["+json(example2)+"]", rv);
	}

}
