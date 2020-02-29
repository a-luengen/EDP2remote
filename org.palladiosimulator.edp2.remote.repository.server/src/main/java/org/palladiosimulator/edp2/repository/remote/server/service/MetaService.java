package org.palladiosimulator.edp2.repository.remote.server.service;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringPointDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringTypeDTO;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.springframework.stereotype.Component;

@Component
public class MetaService {

	private final static ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
	private final static MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;
	
	
	/**
	 * Creates a new ExperimentGroup inside the referenced Repository with 
	 * the groupName as the Purpose of the ExperimentGroup.
	 * In addition a MeasuringPointRepository is created in the ExperimentGroup.
	 * @param repo 
	 * 			Repository where the ExperimentGroup is added to
	 * @param groupName 
	 * 			Set as purpose of the ExperimentGroup
	 * @return Reference to the new created ExperimentGroup
	 */
	public ExperimentGroup createExperimentGroup(Repository repo, String groupName) {
		ExperimentGroup expGrp = EXPERIMENT_DATA_FACTORY.createExperimentGroup();
		
		expGrp.setPurpose(groupName);
		expGrp.setRepository(repo);
		
		MeasuringPointRepository mpRepo = MEASURING_POINT_FACTORY.createMeasuringPointRepository();
		
		if(expGrp.getMeasuringPointRepositories() != null 
				&& expGrp.getMeasuringPointRepositories().isEmpty()) {
			expGrp.getMeasuringPointRepositories().add(mpRepo);
		}
		
		return expGrp;
	}
	
	/**
	 * Returns the ExperimentGroup related with the API-UUID within the referenced Repository.
	 * Returns null if there is no ExperimentGroup found in the Repository, or the API-UUID is
	 * null or invalid.
	 * @param repo 
	 * 			Repository where ExperimentGroup is searched.
	 * @param expGrpUuid 
	 * 			API-UUID related to the ExperimentGroup
	 * @return Reference to the ExperimentGroup or null, if non is found or the API-UUID is invalid.
	 */
	public ExperimentGroup getExperimentGroupFromRepository(Repository repo, String expGrpUuid) {
		if(expGrpUuid == null || expGrpUuid.isEmpty())
			return null;
		String internalId = UUIDConverter.getBase64FromUuid(expGrpUuid);
		
		return repo.getExperimentGroups()
				.stream()
				.filter(grp -> grp.getId().equals(internalId))
				.findAny()
				.orElse(null);
	}
	
	
	/**
	 * Creates a new ExperimentSetting, as specified in the settingDTO. The new ExperimentSetting is added
	 * to the references Repository and the ExperimentGroup, as specified by the related API-UUID of the
	 * ExperimentGroup.
	 * @param repo
	 * 			Reference to the Repository, the ExperimentSetting should be added to.
	 * @param expGrpUuid
	 * 			API-UUID of the ExperimentGroup, the ExperimentSetting should be added to.
	 * @param settingDTO
	 * 			Model describing the ExperimentSetting that should be created.
	 * @return Reference to the newly created ExperimentSetting, or null if the Model is missing,
	 * 			does not contain a description, the ExperimentGroup does not exist within the 
	 * 			Repository.
	 */
	public ExperimentSetting createExperimentSetting(Repository repo, String expGrpUuid, ExperimentSettingDTO settingDTO) {
		
		if(settingDTO == null || settingDTO.getDescription() == null)
			return null;
		
		ExperimentGroup grp = getExperimentGroupFromRepository(repo, expGrpUuid);
		
		if(grp != null) {
			ExperimentSetting setting = EXPERIMENT_DATA_FACTORY.createExperimentSetting(grp, settingDTO.getDescription());
			
			return setting;
		}
		return null;
	}

	/**
	 * Returns the ExperimentSetting specified by the API-UUID that should exist within the 
	 * ExperimentGroup. 
	 * @param expGrp 
	 * 			ExperimentGroup that should contain the ExperimentSettings.
	 * @param settingUuid
	 * 			API-UUID related to the ExperimentSetting, that should be returned.
	 * @return
	 * 			ExperimentSetting related to the API-UUID or null, if non is found.
	 */
	public ExperimentSetting getExperimentSettingFromExperimentGroup(ExperimentGroup expGrp, String settingUuid) {
		String internalId = UUIDConverter.getBase64FromUuid(settingUuid);
		
		ExperimentSetting setting = expGrp.getExperimentSettings()
				.stream()
				.filter(set -> set.getId().matches(internalId))
				.findAny()
				.orElse(null);
		
		return setting;
	}
	
	/**
	 * Creates a new ExperimentRun within the referenced Repository and within the ExperimentGroup and ExperimentSetting
	 * as specified by their related API-UUIDs. The ExperimentRun is created as described by the ExperimentRunDTO.
	 * @param repo
	 * 			Repository where the ExperimentRun is added to.
	 * @param expGrpUuid
	 * 			API-UUID related to the ExperimentGroup where the ExperimentRun is added to.
	 * @param settingUuid
	 * 			API-UUID related to the ExperimentSetting where the ExperimentRun is added to.
	 * @param runDTO
	 * 			Model describing the ExperimentRun.
	 * @return 
	 * 			Reference to created ExperimentRun or null, if there is no ExperimentGroup or ExperimentSetting
	 * 			related to their respective API-UUID.
	 */
	public ExperimentRun createExperimentRun(Repository repo, String expGrpUuid, String settingUuid, ExperimentRunDTO runDTO) {
		ExperimentGroup expGrp = getExperimentGroupFromRepository(repo, expGrpUuid);
		if(expGrp != null) {
			ExperimentSetting setting = getExperimentSettingFromExperimentGroup(expGrp, settingUuid);
			if(setting != null) {
				ExperimentRun run = EXPERIMENT_DATA_FACTORY.createExperimentRun(setting);	
				run.setStartTime(runDTO.getStartTime());
				run.setDuration(Measure.valueOf(runDTO.getDuration(), SI.SECOND));
				return run;
			}
		}		
		return null;
	}
	
	/**
	 * Returns the ExperimentRun related to the given API-UUID that should be contained in the ExperimentSetting.
	 * @param setting
	 * 			ExperimentSetting that should contain the ExperimentRun.
	 * @param runUuid
	 * 			API-UUID related to the ExperimentRun
	 * @return
	 * 			Reference to the ExperimentRun or null, if there is no ExperimentRun related the the API-UUID
	 * 			in the given ExperimentSetting.
	 */
	public ExperimentRun getExperimentRunFromExperimentSetting(ExperimentSetting setting, String runUuid) {
		String internalId = UUIDConverter.getUuidFromBase64(runUuid);
		
		ExperimentRun run = setting.getExperimentRuns()
				.stream()
				.filter(r -> r.getId().matches(internalId))
				.findAny()
				.orElse(null);
		
		return run;
	}
	
	/**
	 * Creates a new StringMeasuringPoint within the ExperimentGroup as modeled by the MeasuringPointDTO.
	 * @param grp
	 * 			ExperimentGroup the StringMeasuringPoint should be created in.
	 * @param dto
	 * 			Model specifying the StringMeasruingPoint to create.
	 * @return
	 * 			Reference to the new created StringMeasuringPoint instance or null, if the ExperimentGroup
	 *			or the MeasuringPointDTO is null.
	 */
	public StringMeasuringPoint createStringMeasuringPoint(ExperimentGroup grp, MeasuringPointDTO dto) {
		if(grp == null || dto == null) {
			return null;
		}
		
		StringMeasuringPoint smp = MEASURING_POINT_FACTORY.createStringMeasuringPoint();
		
		if( smp != null) {
			smp.setMeasuringPoint(dto.getMeasuringPointDescription());
			
			// only one MP-Repo per Group
			MeasuringPointRepository mpRepo = grp.getMeasuringPointRepositories().get(0);
			
			if(mpRepo == null) {
				mpRepo = MEASURING_POINT_FACTORY.createMeasuringPointRepository();
				grp.getMeasuringPointRepositories().add(mpRepo);
			}
			smp.setMeasuringPointRepository(mpRepo);
		}
		return null;
	}

	public MeasuringType createMeasuringType(ExperimentGroup expGrp, MeasuringTypeDTO mtDTO) {
		
		// 1. get measuring point from MeasuringPointRepository
		MeasuringPoint mPoint = null;
		for(MeasuringPoint point: getMeasuringPointsFromExperimentGroup(expGrp)) {
			if(point.getStringRepresentation().equals(mtDTO.getMeasuringPointStringRepresentation())) {
				mPoint = point;
				break;
			}
		}
		MetricDescription description = MetricDescriptionConstants.AGGREGATED_COST_OVER_TIME;
		
		// 2. Create Metric Description
		
		MeasuringType mType = null;
		return null;
	}
	
	public EList<MeasuringPoint> getMeasuringPointsFromExperimentGroup(ExperimentGroup grp) {
		return grp.getMeasuringPointRepositories().get(0).getMeasuringPoints();
	}	
	
}
