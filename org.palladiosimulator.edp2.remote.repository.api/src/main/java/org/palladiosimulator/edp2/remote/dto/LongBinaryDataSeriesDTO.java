package org.palladiosimulator.edp2.remote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="LongBinaryDataSeries",
		description="Representation of a batch of raw measured data of type long."
	)
public class LongBinaryDataSeriesDTO {

	@Schema(
			description = "Id of the Repository, the Data should be stored to. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String repoId;
	
	@Schema(
			description = "Id of the ExperimentGroup, the Data should be stored to. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String grpId;
	
	@Schema(
			description = "Id of the ExperimentSetting, the Data should be stored to. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String settingId;
	
	@Schema(
			description = "Id of the ExperimentRun, the Data should be stored to. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String runId;
	
	@Schema(
			description = "Id of the Measuremetn, the Data should be stored to. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String measurementId;
	
	@Schema(
			description = "Batch of Raw measured data of type long.",
			required = true
		)
	private long[] values;

	public String getRepoId() {
		return repoId;
	}

	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}

	public String getGrpId() {
		return grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getSettingId() {
		return settingId;
	}

	public void setSettingId(String settingId) {
		this.settingId = settingId;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getMeasurementId() {
		return measurementId;
	}

	public void setMeasurementId(String measurementId) {
		this.measurementId = measurementId;
	}

	public long[] getValues() {
		return values;
	}

	public void setValues(long[] values) {
		this.values = values;
	}
	
}
