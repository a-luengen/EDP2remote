package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="RepositoryInfo",
		description="Basic information about a repository. Contains its name and references to all its registerd ExperimentGroups."
	)
public class RepositoryInfoDTO {
	
	@Schema(
			description = "Name of the Repository the information belongs to."
		)
	private String name;
	
	@Schema(
			description = "Id of the Repository. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String id;
	
	@Schema(
			description = "References to the ExperimentGroups the Repository contains. Hex-encoded and in Guid format."
		)
	private List<String> experimentGroupIds;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

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
