package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

public class ExperimentGroupDTO {

	private String uuid;
	private String purpose;

	private List<String> measurementRepositoryIDs;
	
	private List<String> measuringTypeIDs;
	
	private List<String> experimentSettingIDs;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public List<String> getMeasurementRepositoryIDs() {
		return measurementRepositoryIDs;
	}

	public void setMeasurementRepositoryIDs(List<String> measurementRepositoryIDs) {
		this.measurementRepositoryIDs = measurementRepositoryIDs;
	}

	public List<String> getMeasuringTypeIDs() {
		return measuringTypeIDs;
	}

	public void setMeasuringTypeIDs(List<String> measuringTypeIDs) {
		this.measuringTypeIDs = measuringTypeIDs;
	}

	public List<String> getExperimentSettingIDs() {
		return experimentSettingIDs;
	}

	public void setExperimentSettingIDs(List<String> experimentSettingIDs) {
		this.experimentSettingIDs = experimentSettingIDs;
	}
}
