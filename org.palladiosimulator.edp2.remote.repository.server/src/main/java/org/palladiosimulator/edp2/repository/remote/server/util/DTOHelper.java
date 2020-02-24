package org.palladiosimulator.edp2.repository.remote.server.util;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;

public class DTOHelper {

	
	public static RepositoryInfoDTO getRepositoryInfoDTO(Repository repo, RepositoriesService repoService, String routePrefix) {
		RepositoryInfoDTO dto = new RepositoryInfoDTO();
		
		dto.setId(repoService.getApiIdfromRepo(repo));
		dto.setName(routePrefix + "/" + dto.getId());
		
		List<String> expGrpList = new ArrayList<String>();
		for (ExperimentGroup group : repo.getExperimentGroups())
			expGrpList.add(group.getId());
		
		dto.setExperimentGroupIds(expGrpList);
			
		return dto;
	}
	
	public static ExperimentGroupDTO getExperimentGroupDTO(ExperimentGroup expGrp) {
		ExperimentGroupDTO dto = new ExperimentGroupDTO();
		
		dto.setPurpose(expGrp.getPurpose());
		dto.setUuid(expGrp.getId());

		List<String> settingIds = new ArrayList<String>();
		for (ExperimentSetting setting : expGrp.getExperimentSettings()) {
			settingIds.add(setting.getId());
		}
		dto.setExperimentSettingIDs(settingIds);

		List<String> mpIds = new ArrayList<String>();
		for (MeasuringPointRepository mpr : expGrp.getMeasuringPointRepositories()) {
			mpIds.add(mpr.getId());
		}
		
		List<String> mtIds = new ArrayList<String>();
		for (MeasuringType mt : expGrp.getMeasuringTypes()) {
			mtIds.add(mt.getId());
		}
		
		return dto;
	}

	public static ExperimentSettingDTO getExperimentSettingDTO(ExperimentSetting setting) {
		ExperimentSettingDTO dto = new ExperimentSettingDTO();
		
		// get description
		dto.setDescription(setting.getDescription());
		
		// get id
		dto.setId(setting.getId());
		
		// get groupId
		dto.setGroupId(setting.getExperimentGroup().getId());
		
		// get experimentRunIds
		List<String> expRunIds = new ArrayList<String>();
		setting.getExperimentRuns().stream().forEach(run -> expRunIds.add(run.getId()));
		
		return dto;
	}

	public static ExperimentRunDTO getExperimentRunDTO(ExperimentRun run) {
		ExperimentRunDTO dto = new ExperimentRunDTO();
		
		dto.setId(run.getId());
		
		dto.setStartTime(run.getStartTime());
		
		dto.setDuration(run.getDuration().intValue(run.getDuration().getUnit()));
		
		List<String> measurements = new ArrayList<String>();
		for (Measurement measure : run.getMeasurement()) {
			measurements.add(measure.getId());
		}
		
		//dto.setMeasurements(measurements);
		
		
		return dto;
	}
	
	
	
}
