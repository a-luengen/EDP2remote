package org.palladiosimulator.edp2.remote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name="TextualBaseMetricDescription",
		description="Representation of  a TextualBaseMetricDescription."
	)
public class TextualBaseMetricDescriptionDTO {

	@Schema(
			description = "Id of the TextualBaseMatricDescription. Hex-encoded and in guid format.",
			required = true,
			format = "uuid"
		)
	private String id;
	
	@Schema(
			description = "Scale of the Base Metric.",
			required = true
		)
	private String scale;
	
	@Schema(
			description = "Name of the TextualBaseMetricDescription."
		)
	private String name;
	
	@Schema(
			description = "Textual description of the Base Metric."
		)
	private String textDescription;
	
	@Schema(
			description = "Datatype of the Base Metric.",
			required = true
		)
	private String dataType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTextDescription() {
		return textDescription;
	}
	public void setTextDescription(String textDescription) {
		this.textDescription = textDescription;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
