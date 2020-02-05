package org.palladiosimulator.edp2.repository.remote.server.resources;


import org.junit.BeforeClass;

import io.restassured.RestAssured;


/**
 * Base Test Class to setup connection information for the 
 * REST-API-Endpoint.
 * @author Alex
 *
 */
class RepositoryTest {

	@BeforeClass
	public static void setup() {
		
		String port = System.getProperty("server.port");
		if(port == null) {
			RestAssured.port = Integer.valueOf(8080);
		} else {
			RestAssured.port = Integer.valueOf(port);
		}
		
		String basePath = System.getProperty("server.base");
		if(basePath == null) {
			basePath = "/edp2/";
		}
		RestAssured.basePath = basePath;
		
		String baseHost = System.getProperty("server.host");
		if(baseHost == null) {
			baseHost = "http://localhost";
		}
		RestAssured.baseURI = baseHost;
	}

}
