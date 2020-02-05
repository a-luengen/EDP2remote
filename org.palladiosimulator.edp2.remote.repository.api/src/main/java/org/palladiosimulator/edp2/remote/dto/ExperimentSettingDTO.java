package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

public class ExperimentSettingDTO {

	private String purpose;
	
	private String uuid;
	
	private String groupId;
	
	private List<String> experimentRunIds;

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	
	
	
}
