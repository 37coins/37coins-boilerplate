package org.restnucleus;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restnucleus.dao.Model;
import org.restnucleus.stub.Example;
import org.restnucleus.stub.ExampleApplication;
import org.restnucleus.stub.ExampleEntityResource;
import org.restnucleus.test.AbstractDataHelper;

import com.jayway.restassured.http.ContentType;

/**
 * 
 * integration test for entity resource
 * implement according to this: http://www.baeldung.com/2012/01/18/rest-pagination-in-spring/#httpheaders
 * and this: https://www.google.co.kr/url?sa=t&rct=j&q=&esrc=s&source=web&cd=4&ved=0CEgQFjAD&url=https%3A%2F%2Fs3.amazonaws.com%2Ftfpearsonecollege%2Fbestpractices%2FRESTful%2BBest%2BPractices.pdf&ei=8YUhUfidEsHhiAL7joHgAw&usg=AFQjCNHa686kYGEm8oU9wcGvvcvn7voo0Q&sig2=GktG0d1TDwgbk-2ywmHCjQ&bvm=bv.42661473,d.cGE&cad=rja
 * @author johba
 */
public class EntityResourceTest extends AbstractDataHelper {
	public static List<Example> list = null;
	
	@Override
	public JaxRsApplication getApp(Context c) {
		return new ExampleApplication(c);
	}
	
	@Override
	public Map<Class<? extends Model>, List<? extends Model>> getData() {
		List<Example> rv = new ArrayList<Example>();
		rv.add(new Example().setEmail("test0@jb.com"));
		Map<Class<? extends Model>, List<? extends Model>> data = new HashMap<Class<? extends Model>, List<? extends Model>>();
		data.put(Example.class, rv);
		EntityResourceTest.list = rv;
		return data;
	}

	@Test
	public void testCRUD() throws Exception {
		// fire get successfully
		String rv = given()
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.get(restUrl + ExampleEntityResource.PATH_ENTITY,
						list.get(0).getId()).asString();
		Assert.assertTrue(json(list.get(0)).equals(rv));

		// test put
		Example example2 = new Example().setEmail("test2@johba.com");
		example2.setId(Long.parseLong(given()
				.body(json(example2))
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.when()
				.put(restUrl + ExampleEntityResource.PATH_ENTITY,
						list.get(0).getId()).asString()));
		// test get
		rv = given()
				.contentType(ContentType.JSON)
				.expect()
				.statusCode(200)
				.body("email", equalTo(example2.getEmail()))
				.when()
				.get(restUrl + ExampleEntityResource.PATH_ENTITY,
						list.get(0).getId()).asString();
		// test delete
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(204)
				.when()
				.delete(restUrl + ExampleEntityResource.PATH_ENTITY,
						list.get(0).getId());
		// test get
		given().contentType(ContentType.JSON)
				.expect()
				.statusCode(404)
				.when()
				.get(restUrl + ExampleEntityResource.PATH_ENTITY,
						list.get(0).getId()).asString();
	}
}
