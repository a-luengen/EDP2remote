package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="ExperimentGroup", 
		description="Representation of a Group of several Experiments. Contains references to its MeasuringpointRepository, its MeasuringTypes and Setting of the Experiments."
	)
public class ExperimentGroupDTO {

	@Schema(
			description = "Id of the ExperimentGroup. Hex-Encoded and in Guid format.",
			format = "uuid",
			required = true
		)
	private String uuid;
	
	@Schema(
			description = "Optional purpose of this ExperimentGroup."
		)
	private String purpose;

	@Schema(
			description = "References to the MeasuringPointRepositories belonging to this group.",
			format = "uuid"
		)
	private List<String> measuringpointRepositoryIDs;
	
	@Schema(
			description = "References to the MeasuringTypes belonging to this group.",
			format = "uuid"
		)
	private List<String> measuringTypeIDs;
	
	@Schema(
			description = "References to the ExperimentSettings belongign to this group.",
			format = "uuid"
		)
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

	public List<String> getMeasuringpointRepositoryIDs() {
		return measuringpointRepositoryIDs;
	}

	public void setMeasuringpointRepositoryIDs(List<String> measurementRepositoryIDs) {
		this.measuringpointRepositoryIDs = measurementRepositoryIDs;
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
