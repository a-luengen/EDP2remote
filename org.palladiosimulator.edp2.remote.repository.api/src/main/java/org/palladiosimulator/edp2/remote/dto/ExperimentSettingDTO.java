package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="ExperimentSetting",
		description="Representation of a single setting of an experiment. Contains references to the experimentRuns and to MeasuringTypes used by the experiments."
	)
public class ExperimentSettingDTO {

	@Schema(
			description = "Textual description of the Experiments Setting."
		)
	private String description;
	
	@Schema(
			description = "Id of the ExperimentSetting. Hex-encoded and in Guid format.",
			format = "uuid",
			required = true
		)
	private String id;
	
	@Schema(
			description = "Id of the Group the ExperimentSetting belongs to. Hex-encoded and in Guid format.",
			format = "uuid"
		)
	private String groupId;
	
	@Schema(
			description = "References to the ExperimentRuns using this Setting."
		)
	private List<String> experimentRunIds;
	
	@Schema(
			description = "Referneces to the MeasuringTypes used in this Setting."
		)
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
