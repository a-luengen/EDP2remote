package org.palladiosimulator.edp2.repository.remote.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasurementDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringPointDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringTypeDTO;
import org.palladiosimulator.edp2.remote.dto.TextualBaseMetricDescriptionDTO;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.DataType;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricDescriptionRepository;
import org.palladiosimulator.metricspec.Scale;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.metricspec.impl.MetricDescriptionImpl;
import org.palladiosimulator.metricspec.util.builder.TextualBaseMetricDescriptionBuilder;
import org.springframework.stereotype.Component;

@Component
public class MetaService {

	private final static ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
	private final static MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;	
	
	private static List<MetricDescription> metricDescriptions;
	
	/**
	 * Creates a new ExperimentGroup inside the referenced Repository with the
	 * groupName as the Purpose of the ExperimentGroup. In addition a
	 * MeasuringPointRepository is created in the ExperimentGroup.
	 * 
	 * @param repo      Repository where the ExperimentGroup is added to
	 * @param groupName Set as purpose of the ExperimentGroup
	 * @return Reference to the new created ExperimentGroup
	 */
	public ExperimentGroup createExperimentGroup(Repository repo, String groupName) {
		ExperimentGroup expGrp = EXPERIMENT_DATA_FACTORY.createExperimentGroup();

		expGrp.setPurpose(groupName);
		expGrp.setRepository(repo);

		MeasuringPointRepository mpRepo = MEASURING_POINT_FACTORY.createMeasuringPointRepository();

		if (expGrp.getMeasuringPointRepositories() != null && expGrp.getMeasuringPointRepositories().isEmpty()) {
			expGrp.getMeasuringPointRepositories().add(mpRepo);
		}

		return expGrp;
	}

	/**
	 * Returns the ExperimentGroup related with the API-UUID within the referenced
	 * Repository. Returns null if there is no ExperimentGroup found in the
	 * Repository, or the API-UUID is null or invalid.
	 * 
	 * @param repo       Repository where ExperimentGroup is searched.
	 * @param expGrpUuid API-UUID related to the ExperimentGroup
	 * @return Reference to the ExperimentGroup or null, if non is found or the
	 *         API-UUID is invalid.
	 */
	public ExperimentGroup getExperimentGroupFromRepository(Repository repo, String expGrpUuid) {
		if (expGrpUuid == null || expGrpUuid.isEmpty())
			return null;
		String internalId = UUIDConverter.getBase64FromHex(expGrpUuid);

		return repo.getExperimentGroups().stream().filter(grp -> grp.getId().equals(internalId)).findAny().orElse(null);
	}

	/**
	 * Creates a new ExperimentSetting, as specified in the settingDTO. The new
	 * ExperimentSetting is added to the references Repository and the
	 * ExperimentGroup, as specified by the related API-UUID of the ExperimentGroup.
	 * 
	 * @param repo       Reference to the Repository, the ExperimentSetting should
	 *                   be added to.
	 * @param expGrpUuid API-UUID of the ExperimentGroup, the ExperimentSetting
	 *                   should be added to.
	 * @param settingDTO Model describing the ExperimentSetting that should be
	 *                   created.
	 * @return Reference to the newly created ExperimentSetting, or null if the
	 *         Model is missing, does not contain a description, the ExperimentGroup
	 *         does not exist within the Repository.
	 */
	public ExperimentSetting createExperimentSetting(Repository repo, String expGrpUuid,
			ExperimentSettingDTO settingDTO) {

		if (settingDTO == null || settingDTO.getDescription() == null)
			return null;

		ExperimentGroup grp = getExperimentGroupFromRepository(repo, expGrpUuid);

		if (grp != null) {
			ExperimentSetting setting = EXPERIMENT_DATA_FACTORY.createExperimentSetting(grp,
					settingDTO.getDescription());

			return setting;
		}
		return null;
	}

	/**
	 * Returns the ExperimentSetting specified by the API-UUID that should exist
	 * within the ExperimentGroup.
	 * 
	 * @param expGrp      ExperimentGroup that should contain the
	 *                    ExperimentSettings.
	 * @param settingUuid API-UUID related to the ExperimentSetting, that should be
	 *                    returned.
	 * @return ExperimentSetting related to the API-UUID or null, if non is found.
	 */
	public ExperimentSetting getExperimentSettingFromExperimentGroup(ExperimentGroup expGrp, String settingUuid) {
		String internalId = UUIDConverter.getBase64FromHex(settingUuid);

		return expGrp.getExperimentSettings().stream()
				.filter(set -> set.getId().matches(internalId))
				.findAny()
				.orElse(null);
	}

	/**
	 * Creates a new ExperimentRun within the referenced Repository and within the
	 * ExperimentGroup and ExperimentSetting as specified by their related
	 * API-UUIDs. The ExperimentRun is created as described by the ExperimentRunDTO.
	 * 
	 * @param repo        Repository where the ExperimentRun is added to.
	 * @param expGrpUuid  API-UUID related to the ExperimentGroup where the
	 *                    ExperimentRun is added to.
	 * @param settingUuid API-UUID related to the ExperimentSetting where the
	 *                    ExperimentRun is added to.
	 * @param runDTO      Model describing the ExperimentRun.
	 * @return Reference to created ExperimentRun or null, if there is no
	 *         ExperimentGroup or ExperimentSetting related to their respective
	 *         API-UUID.
	 */
	public ExperimentRun createExperimentRun(Repository repo, String expGrpUuid, String settingUuid,
			ExperimentRunDTO runDTO) {
		ExperimentGroup expGrp = getExperimentGroupFromRepository(repo, expGrpUuid);
		if (expGrp != null) {
			ExperimentSetting setting = getExperimentSettingFromExperimentGroup(expGrp, settingUuid);
			if (setting != null) {
				ExperimentRun run = EXPERIMENT_DATA_FACTORY.createExperimentRun(setting);
				run.setStartTime(runDTO.getStartTime());
				run.setDuration(Measure.valueOf(runDTO.getDuration(), SI.SECOND));
				return run;
			}
		}
		return null;
	}

	/**
	 * Returns the ExperimentRun related to the given API-UUID that should be
	 * contained in the ExperimentSetting.
	 * 
	 * @param setting ExperimentSetting that should contain the ExperimentRun.
	 * @param runUuid API-UUID related to the ExperimentRun
	 * @return Reference to the ExperimentRun or null, if there is no ExperimentRun
	 *         related the the API-UUID in the given ExperimentSetting.
	 */
	public ExperimentRun getExperimentRunFromExperimentSetting(ExperimentSetting setting, String runUuid) {
		String internalId = UUIDConverter.getHexFromBase64(runUuid);

		return setting.getExperimentRuns().stream().filter(r -> r.getId().matches(internalId)).findAny()
				.orElse(null);
	}

	/**
	 * Creates a new StringMeasuringPoint within the ExperimentGroup as modeled by
	 * the MeasuringPointDTO.
	 * 
	 * @param grp ExperimentGroup the StringMeasuringPoint should be created in.
	 * @param dto Model specifying the StringMeasruingPoint to create.
	 * @return Reference to the new created StringMeasuringPoint instance or null,
	 *         if the ExperimentGroup or the MeasuringPointDTO is null.
	 */
	public StringMeasuringPoint createStringMeasuringPoint(ExperimentGroup grp, MeasuringPointDTO dto) {
		
		if (grp == null || dto == null) {
			return null;
		}

		StringMeasuringPoint smp = MEASURING_POINT_FACTORY.createStringMeasuringPoint();

		if (smp != null) {
			smp.setMeasuringPoint(dto.getMeasuringPointDescription());
			
			// only one MP-Repo per Group
			MeasuringPointRepository mpRepo = grp.getMeasuringPointRepositories().get(0);

			if (mpRepo == null) {
				mpRepo = MEASURING_POINT_FACTORY.createMeasuringPointRepository();
				grp.getMeasuringPointRepositories().add(mpRepo);
			}
			smp.setMeasuringPointRepository(mpRepo);
			return smp;
		}
		return null;
	}

	/**
	 * Creates a new MeasuringType as described in the MeasuringTypeDTO 
	 * and initializes it with default values and properties.
	 * 
	 * Default Properties of MeasuringType:
	 * 	- TextualBaseMetricDescription as MetricDescription
	 * 		(new instance is created if non is existing, as modeled in mtDTO)
	 *  - StringMeasuringPoint
	 *  	(new instance is created if non is existing, as modeled in mtDTO)
	 * 
	 * @param expGrp 
	 * 			ExperimentGroup containing the MeasuringType
	 * @param mtDTO
	 * 			Model of the MeasuringType, that is created
	 * @return
	 * 			Instance of the new created MeasuringType
	 */
	public MeasuringType createAndInitMeasuringType(final ExperimentGroup expGrp, final MeasuringTypeDTO mtDTO) {

		// 1. get measuring point from MeasuringPointRepository
		MeasuringPoint mPoint = findOrInitStringMeasuringPoint(expGrp, mtDTO.getMeasuringPointStringRepresentation());
			
		// 2. Create Metric Description			
		MetricDescription description = findOrInitMetricDescription(mtDTO.getTextualBaseMetricDescription());
		
		// 3. Create MeasuringType from description and measuringPoint
		MeasuringType mType = EXPERIMENT_DATA_FACTORY.createMeasuringType(mPoint, description);
		expGrp.getMeasuringTypes().add(mType);
		return mType;
	}
	
	public Measurement createAndInitMeasurement(ExperimentRun run, MeasurementDTO mDto) {
		
		MeasuringType mType = getMeasuringTypeFromExperimentSettingById(run.getExperimentSetting(), mDto.getMeasuringTypeId());
		
		if(mType == null)
			return null;
		
		Measurement m = EXPERIMENT_DATA_FACTORY.createMeasurement(mType);
		
		run.getMeasurement().add(m);
		
		MeasurementRange range = EXPERIMENT_DATA_FACTORY.createMeasurementRange(m);
		range.setEndTime(Measure.valueOf(mDto.getEndTime(), SI.MILLI(SI.SECOND)));
		range.setStartTime(Measure.valueOf(mDto.getStartTime(), SI.MILLI(SI.SECOND)));
		
		RawMeasurements rawMeasurement = EXPERIMENT_DATA_FACTORY.createRawMeasurements(range);
		MeasurementsUtility.createDAOsForRawMeasurements(rawMeasurement);
		
		return m;
	}
	
	public Measurement getMeasurementFromExperimentRun(ExperimentRun run, String measurementId) {
		String internalId = UUIDConverter.getBase64FromHex(measurementId);
		
		return run.getMeasurement()
				.stream()
				.filter(m -> m.getId().contentEquals(internalId))
				.findAny()
				.orElse(null);
	}
	
	private MeasuringType getMeasuringTypeFromExperimentSettingById(ExperimentSetting setting, String externalId) {
		String internalId = UUIDConverter.getBase64FromHex(externalId);
		return setting.getMeasuringTypes()
				.stream()
				.filter(mt -> mt.getId() == internalId)
				.findFirst()
				.orElse(null);
	}
	
	private EList<MeasuringPoint> getMeasuringPointsFromExperimentGroup(final ExperimentGroup grp) {
		return grp.getMeasuringPointRepositories().get(0).getMeasuringPoints();
	}
	
	private MeasuringPoint findOrInitStringMeasuringPoint(final ExperimentGroup expGrp, final String stringRepresentation) {
		
		for (MeasuringPoint point : getMeasuringPointsFromExperimentGroup(expGrp)) {
			if (point.getStringRepresentation().equals(stringRepresentation)) {
				return point;
			}
		}
		
		// create new MeasuringPoint and add to ExperimentGroup
		MeasuringPoint mPoint = MEASURING_POINT_FACTORY.createStringMeasuringPoint();
		mPoint.setStringRepresentation(stringRepresentation);
		getMeasuringPointsFromExperimentGroup(expGrp).add(mPoint);
		return mPoint;
	}
	
	private MetricDescription findOrInitMetricDescription(final TextualBaseMetricDescriptionDTO tbmdDto) {
		
		// find existing
		if(metricDescriptions == null) {
			metricDescriptions = new ArrayList<MetricDescription>();
		}

		for(MetricDescription desc : metricDescriptions) {
			if(tbmdDto.getId() == UUIDConverter.getHexFromBase64(desc.getId())) {
				return desc;
			}
		}
		
		// or init new one
		Scale scale = Scale.ORDINAL;
		
		DataType dType = 
				tbmdDto.getDataType().contentEquals("qualitative") 
				? DataType.QUALITATIVE
				: DataType.QUANTITATIVE;
		
		String uuid = UUID.randomUUID().toString();
		
		MetricDescription desc = TextualBaseMetricDescriptionBuilder
			.newTextualBaseMetricDescriptionBuilder()
			.scale(scale)
			.dataType(dType)
			.id(uuid)
			.name(tbmdDto.getName())
			.build();
		metricDescriptions.add(desc);
		return desc;
	}
	
	
	/*
	private MetricDescription findMetricDescriptionConstantById(final String metricDescriptionId) {
		
		
		// TODO - Has to be a better way
		MetricDescription[] METRIC_DESCRIPTIONS = {
				MetricDescriptionConstants.AGGREGATED_COST_OVER_TIME,
				MetricDescriptionConstants.COST_OF_RESOURCE_CONTAINERS,
				MetricDescriptionConstants.COST_OVER_TIME,
				MetricDescriptionConstants.COST_PER_RESOURCE_CONTAINER,
				MetricDescriptionConstants.CUMULATIVE_ENERGY_CONSUMPTION_TUPLE,
				MetricDescriptionConstants.ENERGY_CONSUMPTION,
				MetricDescriptionConstants.EXECUTION_RESULT_METRIC,
				MetricDescriptionConstants.EXECUTION_RESULT_METRIC_TUPLE,
				MetricDescriptionConstants.EXTERNAL_EVENT_TIME_METRIC,
				MetricDescriptionConstants.HDD_READ_RATE,
				MetricDescriptionConstants.HDD_READ_RATE_TUPLE,
				MetricDescriptionConstants.HDD_WRITE_RATE,
				MetricDescriptionConstants.HDD_WRITE_RATE_TUPLE,
				MetricDescriptionConstants.HOLDING_TIME_METRIC,
				MetricDescriptionConstants.HOLDING_TIME_METRIC_TUPLE,
				MetricDescriptionConstants.INTER_ARRIVAL_TIME_CAPACITY,
				MetricDescriptionConstants.INTER_ARRIVAL_TIME_CAPACITY_TUPLE,
				MetricDescriptionConstants.MARGINAL_COST,
				MetricDescriptionConstants.MEAN_TIME_TO_QUALITY_REPAIR,
				MetricDescriptionConstants.NUMBER_OF_CONCURRENTLY_EXECUTING_INVOCATIONS,
				MetricDescriptionConstants.NUMBER_OF_CONCURRENTLY_EXECUTING_INVOCATIONS_TUPLE,
				MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS,
				MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME,
				MetricDescriptionConstants.NUMBER_OF_SLO_VIOLATIONS,
				MetricDescriptionConstants.NUMBER_OF_SLO_VIOLATIONS_OVER_TIME,
				MetricDescriptionConstants.OPTIMISATION_TIME_METRIC,
				MetricDescriptionConstants.OPTIMISATION_TIME_METRIC_TUPLE,
				MetricDescriptionConstants.OVERALL_STATE_OF_ACTIVE_RESOURCE_METRIC,
				MetricDescriptionConstants.OVERALL_STATE_OF_PASSIVE_RESOURCE_METRIC,
				MetricDescriptionConstants.POINT_IN_TIME_METRIC,
				MetricDescriptionConstants.POWER_CONSUMPTION,
				MetricDescriptionConstants.POWER_CONSUMPTION_TUPLE,
				MetricDescriptionConstants.RECONFIGURATION_TIME_METRIC,
				MetricDescriptionConstants.RECONFIGURATION_TIME_METRIC_TUPLE,
				MetricDescriptionConstants.REQUEST_ARRIVAL_RATE,
				MetricDescriptionConstants.REQUEST_ARRIVAL_RATE_TUPLE,
				MetricDescriptionConstants.RESOURCE_DEMAND_METRIC,
				MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE,
				MetricDescriptionConstants.RESPONSE_TIME_METRIC,
				MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE,
				MetricDescriptionConstants.SCALABILITY_RANGE,
				MetricDescriptionConstants.SCALABILITY_SPEED,
				MetricDescriptionConstants.SLOPE,
			};
		
		for (MetricDescription metricDescription : METRIC_DESCRIPTIONS) {
			if(metricDescription.getId().contentEquals(metricDescriptionId))
				return metricDescription;
		}
		return null;
	}
	*/
}
