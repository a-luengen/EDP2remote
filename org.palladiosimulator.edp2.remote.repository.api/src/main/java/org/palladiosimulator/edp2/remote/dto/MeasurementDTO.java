package org.palladiosimulator.edp2.remote.dto;

public class MeasurementDTO {

	private String id;
	
	private String runId;
	
	private long endTime;
	
	private long startTime;
	
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
