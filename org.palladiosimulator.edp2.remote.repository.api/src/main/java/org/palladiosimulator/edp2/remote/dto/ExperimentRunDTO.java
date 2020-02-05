package org.palladiosimulator.edp2.remote.dto;

import java.util.Date;
import java.util.List;

public class ExperimentRunDTO {

	private String id;
	private int duration;
	private Date startTime;
	private List<MeasurementDTO> measurements;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public List<MeasurementDTO> getMeasurements() {
		return measurements;
	}
	public void setMeasurements(List<MeasurementDTO> measurements) {
		this.measurements = measurements;
	}
}
