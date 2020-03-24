package org.palladiosimulator.edp2.remote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="Measurement",
		description="Representaiton of a single Measurement. Contains reference to its measuringType and the experimentRun, it belongs to."
	)
public class MeasurementDTO {

	@Schema(
			description = "Id of the Measurement. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String id;
	
	@Schema(
			description = "Id of the ExperimentRun, this measurement belongs to. Hex-encoded and in Guid format.",
			required = true,
			format = "uuid"
		)
	private String runId;
	
	@Schema(
			description = "Time stamp at the end of the Measurement.",
			required = true
		)
	private long endTime;
	
	@Schema(
			description = "Time stamp at start of the Measurement.",
			required = true
		)
	private long startTime;
	
	@Schema(
			description = "Id of the Type of this Measurement. Hex-encoded and in Guid format.",
			required = true
		)
	private String measuringTypeId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getMeasuringTypeId() {
		return measuringTypeId;
	}

	public void setMeasuringTypeId(String measuringTypeId) {
		this.measuringTypeId = measuringTypeId;
	}
	
}
