package org.palladiosimulator.edp2.remote.dto;

import java.util.List;

public class MeasurementDTO {

	private String id;
	
	private String endTime;
	
	private String startTime;
	
	private String measuringType;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public List<DataSeriesDTO> getDataSeries() {
		return dataSeries;
	}

	public void setDataSeries(List<DataSeriesDTO> dataSeries) {
		this.dataSeries = dataSeries;
	}

	private List<DataSeriesDTO> dataSeries;
}
