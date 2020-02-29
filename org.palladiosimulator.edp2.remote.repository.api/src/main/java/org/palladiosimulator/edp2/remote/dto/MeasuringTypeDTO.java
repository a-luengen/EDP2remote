package org.palladiosimulator.edp2.remote.dto;

public class MeasuringTypeDTO {

	private String id;
	private String measuringPointStringRepresentation;
	private String metricDescription;
	private String experimentGroupId;
	
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
	public String getMetricDescription() {
		return metricDescription;
	}
	public void setMetricDescription(String metricDescription) {
		this.metricDescription = metricDescription;
	}
	public String getExperimentGroupId() {
		return experimentGroupId;
	}
	public void setExperimentGroupId(String experimentGroupId) {
		this.experimentGroupId = experimentGroupId;
	}
	
}
