package org.palladiosimulator.edp2.remote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="MeasuringPoint",
		description="Representation of a MeasuringPoint, currently limited to StringMeasuringPoint only. Contains the description of the MeasuringPoint as string."
	)
public class MeasuringPointDTO {

	@Schema(
			description = "Textual description of the MeasuringPoint.",
			required = true
		)
	private String measuringPointDescription;

	public String getMeasuringPointDescription() {
		return measuringPointDescription;
	}

	public void setMeasuringPointDescription(String measuringPointDescription) {
		this.measuringPointDescription = measuringPointDescription;
	}

}
