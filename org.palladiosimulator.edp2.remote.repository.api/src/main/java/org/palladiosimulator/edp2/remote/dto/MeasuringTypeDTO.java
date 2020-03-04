package org.palladiosimulator.edp2.remote.dto;

public class MeasuringTypeDTO {

	private String id;
	private String experimentGroupId;
	private String measuringPointStringRepresentation;
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
