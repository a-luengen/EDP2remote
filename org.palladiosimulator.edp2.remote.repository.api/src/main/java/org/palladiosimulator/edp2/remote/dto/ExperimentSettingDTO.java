package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

public class ExperimentSettingDTO {

	private String description;
	
	private String id;
	
	private String groupId;
	
	private List<String> experimentRunIds;
	
	private List<String> measuringTypeIds;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getExperimentRunIds() {
		return experimentRunIds;
	}

	public void setExperimentRunIds(List<String> experimentRunIds) {
		this.experimentRunIds = experimentRunIds;
	}

	public List<String> getMeasuringTypeIds() {
		return measuringTypeIds;
	}

	public void setMeasuringTypeIds(List<String> measuringTypeIds) {
		this.measuringTypeIds = measuringTypeIds;
	}	
}
