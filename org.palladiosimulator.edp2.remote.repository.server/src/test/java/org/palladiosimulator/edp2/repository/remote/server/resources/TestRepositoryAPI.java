package org.palladiosimulator.edp2.repository.remote.server.resources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import javax.ws.rs.core.Response.Status;



//@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class TestRepositoryAPI {

	final static String repoUriPath = "/edp2/meta";
	
	
	@BeforeAll
	public static void setup() {
		String portStr = System.getProperty("server.port");
		if(portStr == null) {
			port = Integer.valueOf(8080);
		} else {
			port = Integer.valueOf(portStr);
		}
		
		String basePath = System.getProperty("server.base");
		System.out.println("BASE PATH:");
		System.out.println(basePath);
		if(basePath == null) {
			basePath = "/edp2/meta";
		}
		
		String baseHost = System.getProperty("server.host");
		if(baseHost == null) {
			baseHost = "http://localhost";
		}
		baseURI = baseHost;
	}
	
	@Test
	public void createNewRepository() {
		given().filter(new RequestLoggingFilter()).contentType(ContentType.JSON)
		.post(this.repoUriPath + "/repository")
		.then().statusCode(201)
		.assertThat().body(containsString("id"));
	}
}
