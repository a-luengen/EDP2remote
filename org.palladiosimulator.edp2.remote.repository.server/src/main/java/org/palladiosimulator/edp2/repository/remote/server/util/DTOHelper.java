package org.palladiosimulator.edp2.repository.remote.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.measure.unit.SI;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasurementDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringPointDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringTypeDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;
import org.palladiosimulator.edp2.remote.dto.TextualBaseMetricDescriptionDTO;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;

public class DTOHelper {

	
	public static RepositoryInfoDTO getRepositoryInfoDTO(Repository repo, RepositoriesService repoService, String routePrefix) {
		RepositoryInfoDTO dto = new RepositoryInfoDTO();
		
		dto.setId(repoService.getApiIdfromRepo(repo));
		dto.setName(routePrefix + "/" + UUIDConverter.getHexFromBase64(dto.getId()));
		
		List<String> expGrpList = new ArrayList<String>();
		for (ExperimentGroup group : repo.getExperimentGroups())
			expGrpList.add(UUIDConverter.getHexFromBase64(group.getId()));
		
		dto.setExperimentGroupIds(expGrpList);
			
		return dto;
	}
	
	public static ExperimentGroupDTO getExperimentGroupDTO(ExperimentGroup expGrp) {
		ExperimentGroupDTO dto = new ExperimentGroupDTO();
		
		dto.setPurpose(expGrp.getPurpose());
		dto.setUuid(UUIDConverter.getHexFromBase64(expGrp.getId()));

		List<String> settingIds = new ArrayList<String>();
		for (ExperimentSetting setting : expGrp.getExperimentSettings()) {
			settingIds.add(UUIDConverter.getHexFromBase64(setting.getId()));
		}
		dto.setExperimentSettingIDs(settingIds);

		List<String> mpIds = new ArrayList<String>();
		for (MeasuringPointRepository mpr : expGrp.getMeasuringPointRepositories()) {
			mpIds.add(UUIDConverter.getHexFromBase64(mpr.getId()));
		}
		
		List<String> mtIds = new ArrayList<String>();
		for (MeasuringType mt : expGrp.getMeasuringTypes()) {
			mtIds.add(UUIDConverter.getHexFromBase64(mt.getId()));
		}
		
		return dto;
	}

	public static ExperimentSettingDTO getExperimentSettingDTO(ExperimentSetting setting) {
		ExperimentSettingDTO dto = new ExperimentSettingDTO();
		
		dto.setDescription(setting.getDescription());

		dto.setId(UUIDConverter.getHexFromBase64(setting.getId()));
		
		dto.setGroupId(UUIDConverter.getHexFromBase64(setting.getExperimentGroup().getId()));

		List<String> expRunIds = new ArrayList<String>();
		setting.getExperimentRuns().stream()
			.forEach(run -> expRunIds.add(UUIDConverter.getHexFromBase64(run.getId())));
		
		List<String> mtIds = new ArrayList<String>();
		setting.getMeasuringTypes().stream()
			.forEach(type -> mtIds.add(UUIDConverter.getHexFromBase64(type.getId())));
		
		return dto;
	}

	@SuppressWarnings("unchecked")
	public static ExperimentRunDTO getExperimentRunDTO(ExperimentRun run) {
		ExperimentRunDTO dto = new ExperimentRunDTO();
		
		dto.setId(UUIDConverter.getHexFromBase64(run.getId()));
		
		dto.setStartTime(run.getStartTime());
		
		dto.setDuration(run.getDuration().intValue(SI.MILLI(SI.SECOND)));
		
		List<MeasurementDTO> measurements = new ArrayList<MeasurementDTO>();
		for (Measurement measurement : run.getMeasurement()) {
			measurements.add(getMeasurementDTO(measurement));
		}
		
		dto.setMeasurements(measurements);
		return dto;
	}

	public static MeasuringPointDTO getMeasuringPointDTO(StringMeasuringPoint mp) {
		MeasuringPointDTO dto = new MeasuringPointDTO();
		dto.setMeasuringPointDescription(mp.getStringRepresentation());
		return dto;
	}

	public static MeasuringTypeDTO getMeasuringTypeDTO(MeasuringType mType) {
		MeasuringTypeDTO dto = new MeasuringTypeDTO();
		
		dto.setExperimentGroupId(
				UUIDConverter.getHexFromBase64(
						mType.getExperimentGroup().getId()
						)
				);
		dto.setId(UUIDConverter.getHexFromBase64(mType.getId()));
		dto.setMeasuringPointStringRepresentation(mType.getMeasuringPoint().getStringRepresentation());
		
		// add the textualBaseMetricDTO
		TextualBaseMetricDescriptionDTO tbmdDto = getTextualBaseMetricDescription(
				(TextualBaseMetricDescription) mType.getMetric());
		
		dto.setTextualBaseMetricDescription(tbmdDto);
		
		return dto;
	}
	
	public static TextualBaseMetricDescriptionDTO getTextualBaseMetricDescription(TextualBaseMetricDescription tbmd) {
		TextualBaseMetricDescriptionDTO dto = new TextualBaseMetricDescriptionDTO();
		dto.setDataType(tbmd.getDataType().getName());
		dto.setId(UUIDConverter.getHexFromBase64(tbmd.getId()));;
		dto.setScale(tbmd.getScale().getName());
		dto.setTextDescription(tbmd.getTextualDescription());
		return dto;
	}
	
	@SuppressWarnings("unchecked")
	public static MeasurementDTO getMeasurementDTO(Measurement measurement) {
		MeasurementDTO dto = new MeasurementDTO();
		
		dto.setId(UUIDConverter.getHexFromBase64(measurement.getId()));
		dto.setMeasuringTypeId(UUIDConverter.getHexFromBase64(measurement.getMeasuringType().getId()));
		dto.setRunId(UUIDConverter.getHexFromBase64(measurement.getRun().getId()));
		
		long endTime = measurement.getMeasurementRanges().get(0).getEndTime().longValue(SI.MILLI(SI.SECOND));
		long startTime = measurement.getMeasurementRanges().get(0).getStartTime().longValue(SI.MILLI(SI.SECOND));
		
		dto.setEndTime(endTime);
		dto.setStartTime(startTime);
		
		return dto;
	}
	
	
}
