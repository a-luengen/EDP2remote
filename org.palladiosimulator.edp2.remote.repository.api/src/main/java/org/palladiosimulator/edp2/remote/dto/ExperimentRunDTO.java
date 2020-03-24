package org.palladiosimulator.edp2.remote.dto;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="ExperimentRun", 
		description="Representation of a single Run of an Experiment. Contains representation of all measurements during the run."
	)
public class ExperimentRunDTO {

	
	@Schema(
			description = "Id of the ExperimentRun. Hex-encoded and in Guid format.",
			format = "uuid",
			required = true
		)
	private String id;
	
	@Schema(
			description = "Duration of the Experiment Run.",
			required = true
		)
	private long duration;
	
	@Schema(
			description = "Start time of the Experiment Run.",
			required = true
		)
	private Date startTime;
	
	@Schema(
			description = "References to the Measurements of this Experiment Run.",
			required = true
		)
	private List<MeasurementDTO> measurements;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
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
