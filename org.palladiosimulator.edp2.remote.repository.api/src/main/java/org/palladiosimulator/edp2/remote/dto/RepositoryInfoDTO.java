package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

public class RepositoryInfoDTO {
	
	private String name;
	
	private List<String> experimentGroupIds;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getExperimentGroupIds() {
		return experimentGroupIds;
	}
	public void setExperimentGroupIds(List<String> experimentGroupIds) {
		this.experimentGroupIds = experimentGroupIds;
	}
}
