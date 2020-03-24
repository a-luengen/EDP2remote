package org.palladiosimulator.edp2.remote.repository.server.util;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.metricspec.DataType;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.util.builder.TextualBaseMetricDescriptionBuilder;

public class MetricDescriptionTest {

	@Test
	public void createTextualBasedMetricDescription_shouldRunWithoutException() {
		DataType dType = DataType.QUALITATIVE;
		
		String uuid = UUID.randomUUID().toString();
		
		String name = "Test";
		
		MetricDescription desc = TextualBaseMetricDescriptionBuilder
			.newTextualBaseMetricDescriptionBuilder()
			.dataType(dType)
			.id(uuid)
			.name(name)
			.build();
		
		Assertions.assertNotNull(desc, "Should not be null.");
		Assertions.assertEquals(uuid, desc.getId());
		Assertions.assertEquals(name, desc.getName());
	}
}
