package org.palladiosimulator.edp2.remote.repository.server.util;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;;

public class TestUUIDConverter {

	static String base64 = "_yU3HUFb3Eeq25qWGDdWnag";
	static String uuid = "12c79814-b0c7-4e1b-bf03-7dbecf35ad0a";
	
	@Test
	void convertBase64ToUUID_returnsCorrectUuidString() {
		String testUuid = UUIDConverter.getHexFromBase64(base64);
		
		Assertions.assertTrue(testUuid.length() == 36,"Got Length: " + testUuid.length());
		Assertions.assertTrue(Pattern.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", testUuid), testUuid);		
	}
	
	@Test
	void convertUuidToBase64_returnsCorrectUuidString() {
		String uuid = UUIDConverter.getHexFromBase64(base64);
		String testUuid = UUIDConverter.getBase64FromHex(uuid);
		
		Assertions.assertTrue(Pattern.matches("^_?[-A-Za-z0-9+=]{1,50}", testUuid), testUuid);
	}
	
	@Test
	void converterBase64ToUuidToBase64_shouldReturnSameResult() {
		String[] base64TestExamples = { 
				"_gJnK8VcPEeqJf7CrONiWFQ", base64, 
				"_AiroIZMbEd6Vw8NDgVSYcg", "_BRpvcEigEd-uCvl0Z-GteQ",
				"_lss1MEhpEd-SQI4N8E0NHA", "_JYesoEigEd-4XZQqGmj8Pg",
				"_LCpvcEigEd-s193kEND-BA"};
		String[] base64TestResults = new String[base64TestExamples.length];
		
		for (int i = 0; i < base64TestResults.length; i++) {
			String temp = UUIDConverter.getHexFromBase64(base64TestExamples[i]);
			base64TestResults[i] = UUIDConverter.getBase64FromHex(temp);
		}
		
		for (int i = 0; i < base64TestResults.length; i++) {
			Assertions.assertEquals(base64TestExamples[i], base64TestResults[i], "But got: " + base64TestResults[i]);
		}
	}
	
	/*
	 * Not in use-case
	 * */
	@Test
	void converterUuidEndToEnd_shouldReturnSameResult() {
		String[] uuidTestExamples = { uuid };
		String[] uuidTestResults = new String[uuidTestExamples.length];
		
		for (int i = 0; i < uuidTestResults.length; i++) {
			String temp = UUIDConverter.getBase64FromHex(uuidTestExamples[i]);
			uuidTestResults[i] = UUIDConverter.getHexFromBase64(temp);
		}
		
		for (int i = 0; i < uuidTestResults.length; i++) {
			Assertions.assertEquals(uuidTestExamples[i], uuidTestResults[i], "But got: " + uuidTestResults[i]);
		}
	}
	
	
}
