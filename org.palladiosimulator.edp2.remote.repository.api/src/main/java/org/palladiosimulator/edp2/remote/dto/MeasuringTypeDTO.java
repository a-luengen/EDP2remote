package org.palladiosimulator.edp2.remote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="MeasuringType",
		description="Representation of a MeasuringType. Contains references to the experimentGroup it belongs to. "
				+ "In addition it contains the string representation of the MeasuringPoint and the MetricDescription."
	)
public class MeasuringTypeDTO {

	@Schema(
			description = "Id of the MeasuringType. Hex-encoded and in Guid-format.",
			required = true,
			format = "uuid"
		)
	private String id;
	
	@Schema(
			description = "Id of the ExperimentGroup the MeasuringType belongs to. Hex-encoded and in Guid-format.",
			required = true,
			format = "uuid"
		)
	private String experimentGroupId;
	
	@Schema(
			description = "Textual representation of the MeasuringPoint. Used to find correct Reference.",
			required = true
		)
	private String measuringPointStringRepresentation;
	
	@Schema(
			description = "Textual description of the Base Metric of the MeasuringType.",
			required = true
		)
	private TextualBaseMetricDescriptionDTO textualBaseMetricDescription;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMeasuringPointStringRepresentation() {
		return measuringPointStringRepresentation;
	}
	public void setMeasuringPointStringRepresentation(String measuringPointStringRepresentation) {
		this.measuringPointStringRepresentation = measuringPointStringRepresentation;
	}

	public String getExperimentGroupId() {
		return experimentGroupId;
	}
	public void setExperimentGroupId(String experimentGroupId) {
		this.experimentGroupId = experimentGroupId;
	}
	public TextualBaseMetricDescriptionDTO getTextualBaseMetricDescription() {
		return textualBaseMetricDescription;
	}
	public void setTextualBaseMetricDescription(TextualBaseMetricDescriptionDTO textualBaseMetricDescription) {
		this.textualBaseMetricDescription = textualBaseMetricDescription;
	}
	
}
