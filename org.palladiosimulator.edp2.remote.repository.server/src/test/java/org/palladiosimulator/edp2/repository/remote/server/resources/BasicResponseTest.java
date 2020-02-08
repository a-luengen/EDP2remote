package org.palladiosimulator.edp2.repository.remote.server.resources;

//import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;
//import static io.restassured.matcher.RestAssuredMatchers.*;
//import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

class BasicResponseTest extends BaseTest {

	@Test
	void basicPingTest() {
		given().when().get("").then().statusCode(200);
	}
}
