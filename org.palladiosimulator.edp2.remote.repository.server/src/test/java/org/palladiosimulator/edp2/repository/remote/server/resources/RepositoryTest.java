package org.palladiosimulator.edp2.repository.remote.server.resources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.*;



@SpringBootTest
public class RepositoryTest {

	final static String repoUriPath = "/meta/repository";
	
	
	@BeforeAll
	public void setup() {
		String portStr = System.getProperty("server.port");
		if(portStr == null) {
			port = Integer.valueOf(8080);
		} else {
			port = Integer.valueOf(portStr);
		}
		
		String basePath = System.getProperty("server.base");
		if(basePath == null) {
			basePath = "/edp2/meta/repo/";
		}
		//basePath = basePath;
		
		String baseHost = System.getProperty("server.host");
		if(baseHost == null) {
			baseHost = "http://localhost";
		}
		baseURI = baseHost;
	}
	
	@Test
	public void postRepository_returnOk() {
		
	}
}
