package org.restnucleus;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restnucleus.dao.Model;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.filter.RsqlFilter;
import org.restnucleus.stub.Example;
import org.restnucleus.stub.ExampleApplication;
import org.restnucleus.stub.ExampleCollectionResource;
import org.restnucleus.stub.ExampleEntityResource;
import org.restnucleus.test.AbstractDataHelper;

import com.jayway.restassured.http.ContentType;
import com.strategicgains.util.date.DateAdapter;

/**
 * 
 * integration test for collection resource
 * 
 * @author johba
 */
public class ExampleTest extends AbstractDataHelper {
	public static List<Example> list = null;
	public static Date ONE = null;
	public static Date TWO = null;
	public static Date THREE = null;
	public static Date FOUR = null;
	public static Date FIVE = null;

	static {
		try {
			DateAdapter da = new DateAdapter();
			ONE = da.parse("2013-02-01T00:00Z");
			TWO = da.parse("2013-02-02T00:00Z");
			THREE = da.parse("2013-02-03T00:00Z");
			FOUR = da.parse("2013-02-04T00:00Z");
			FIVE = da.parse("2013-02-05T00:00Z");
		} catch (ParseException e) {
		}
	}
	@Override
	public Map<Class<? extends Model>, List<? extends Model>> getData() {
		List<Example> rv = new ArrayList<>();
		rv.add((Example)new Example().setEmail("test0@jb.com").setCreationTime(ONE));
		rv.add((Example)new Example().setEmail("test1@jb.com").setCreationTime(THREE));
		rv.add((Example)new Example().setEmail("test2@jb.com").setCreationTime(FIVE));
		Map<Class<? extends Model>, List<? extends Model>> data = new HashMap<>();
		data.put(Example.class, rv);
		ExampleTest.list = rv;
		return data;
	}

	@Override
	public JaxRsApplication getApp(Context c) {
		return new ExampleApplication(c);
	}
	
	@Test
	public void testCRUD() throws Exception {
		Example e = new Example().setEmail("test2@johba.de");
		e.setId(Long.parseLong(given()
			.body(json(e))
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(200)
		.when()
			.post(restUrl + ExampleCollectionResource.PATH).asString()));
		
		// fire get successfully
		given()
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(200)
			.body("email", equalTo(e.getEmail()))
		.when()
			.get(restUrl + ExampleEntityResource.PATH_ENTITY, e.getId());

		// test put
		given()
			.body(json(e.setEmail("test@johba.de")))
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(200)
		.when()
			.put(restUrl + ExampleEntityResource.PATH_ENTITY, e.getId());
		// test get
		given()
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(200)
			.body("email", equalTo(e.getEmail()))
		.when()
			.get(restUrl + ExampleEntityResource.PATH_ENTITY, e.getId());
		
		// test delete
		given()
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(204)
		.when()
			.delete(restUrl + ExampleEntityResource.PATH_ENTITY, e.getId());
		
		// test get
		given()
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(404)
		.when()
			.get(restUrl + ExampleEntityResource.PATH_ENTITY, e.getId()).asString();
	}

	@Test
	public void testQuery() throws Exception {
		// no paging, no parameters
		given()
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(200)
			.body("size()", is(3)).and()
			.body("email", hasItems("test0@jb.com","test1@jb.com","test2@jb.com"))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);
		// paging, no parameters
		given()
			.contentType(ContentType.JSON)
			.param(RNQuery.PAGE, "2").and()
			.param(RNQuery.SIZE, "1")
		.expect()
			.statusCode(200)
			.body("size()", is(1)).and()
			.body("email", hasItem("test2@jb.com"))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);
		// // wrong paging
		// given().contentType(ContentType.JSON)
		// .expect()
		// .statusCode(400)
		// .when()
		// .get(restUrl
		// + ExampleCollectionResource.PATH_PAGINATION, 2,
		// -1);
		// paging, one paramter
		Example e = ExampleTest.list.get(2);
		given()
			.contentType(ContentType.JSON)
			.param("filter", "email::"+ e.getEmail())
		.expect()
			.statusCode(200)
			.body("size()", is(1)).and()
			.body("email", hasItem("test2@jb.com"))
		.when().get(restUrl + ExampleCollectionResource.PATH);
		// TODO: paging, two parameter

		// TODO: no paging, but parameter

		// TODO: injection attack, to check query sanity
		
		// TODO: test jsonp wrapper
	}
	// @Test
	// public void testObjectQuery() {
	// String rv = given().body(json(CollectionResourceTest.list.get(0)))
	// .contentType(ContentType.JSON).expect().statusCode(200).when()
	// .put(restUrl + ExampleCollectionResource.PATH).asString();
	// Assert.assertEquals("[" + json(CollectionResourceTest.list.get(2)) + "]",
	// rv);
	// }

	@Test
	public void testTime() throws Exception {
		// query with date before
		given()
			.param("before", new DateAdapter().format(TWO))
		.expect()
			.statusCode(200)
			.body("size()", is(1)).and()
			.body("email", hasItem("test0@jb.com"))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);
		// query with date after
		given()
			.param("after", new DateAdapter().format(FOUR))
		.expect()
			.statusCode(200)
			.body("size()", is(1)).and()
			.body("email", hasItem("test2@jb.com"))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);
		// query with date after + before
		given()
			.param("after", new DateAdapter().format(TWO)).and()
			.param("before", new DateAdapter().format(FOUR))
		.expect()
			.statusCode(200)
			.body("size()", is(1)).and()
			.body("email", hasItem("test1@jb.com"))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);		
	}

	@Test
	public void testQueryDelete() throws Exception {
		Example e = new Example().setEmail("test5@johba.de");
		e.setId(Long.parseLong(given()
			.body(json(e))
			.contentType(ContentType.JSON)
		.expect()
			.statusCode(200)
		.when()
			.post(restUrl + ExampleCollectionResource.PATH).asString()));
		//delete query
		given()
			.contentType(ContentType.JSON)
			.param("filter", "email::"+e.getEmail())
		.expect()
			.statusCode(204)
		.when()
			.delete(restUrl + ExampleCollectionResource.PATH);
		//check again, should be gone
		given()
			.contentType(ContentType.JSON)
			.param("filter", "email::"+e.getEmail())
		.expect()
			.statusCode(200)
			.body("size()", is(0))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);
	}
	
	@Test
	public void testRSQL(){
		given()
			.contentType(ContentType.JSON)
			.queryParam(RsqlFilter.RSQ, "email=="+list.get(0).getEmail()+",email=="+list.get(1).getEmail())
		.expect()
			.statusCode(200)
			.body("size()", is(2))
		.when()
			.get(restUrl + ExampleCollectionResource.PATH);
	}
}
