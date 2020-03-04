package org.palladiosimulator.edp2.remote.repository.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import org.palladiosimulator.edp2.remote.dto.TextualBaseMetricDescriptionDTO;
import org.palladiosimulator.edp2.repository.remote.server.resources.UUIDGenerator;
import org.palladiosimulator.edp2.repository.remote.server.service.MetaService;
import org.palladiosimulator.edp2.repository.remote.server.util.DTOHelper;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.metricspec.util.builder.TextualBaseMetricDescriptionBuilder;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class TestMetaService extends BasicEDP2Test {

	static MetaService mService = new MetaService();
	
	@Test
	void createNewExperimentGroup_shouldBeContainedInRepositoryandContainsPurposeAndMeasuringPointRepository() {
		Repository ldRepo = repoService.createNewRepository();
		
		String testPurpose = "test";
		
		ExperimentGroup grp = mService.createExperimentGroup(ldRepo, testPurpose);
		
		assertThat("New ExperimentGroup should be in Repository", ldRepo.getExperimentGroups(), hasItem(grp));
		Assertions.assertNotNull(grp.getMeasuringPointRepositories().get(0), "Contains a MeasuringPointRepository.");
		Assertions.assertTrue(grp.getMeasuringPointRepositories().size() == 1, "Contains exactly one MeasuringPointRepository.");
		Assertions.assertEquals(testPurpose, grp.getPurpose());
	}
	
	@Test
	void getExperimentGroupFromRepo_shouldReturnCorrectExperimentGroup() {
		Repository ldRepo = repoService.createNewRepository();
		
		ExperimentGroup grp = mService.createExperimentGroup(ldRepo, "");
		
		String grpUuid = UUIDConverter.getHexFromBase64(grp.getId());
		
		ExperimentGroup testGrp = mService.getExperimentGroupFromRepository(ldRepo, grpUuid);
		
		Assertions.assertEquals(grp, testGrp);
	}
	
	@Test
	void createExperimentSetting_shouldReturnExperimentSettingContainedInExperimentGroup() {
			
		Repository repo = repoService.createNewRepository();
		ExperimentGroup expGrp = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
		repo.getExperimentGroups().add(expGrp);
		
		for(int i = 0; i < 10; i++) {
			ExperimentGroup temp = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
			repo.getExperimentGroups().add(temp);
		}
		
		String groupUuid = UUIDConverter.getHexFromBase64(expGrp.getId());
		
		ExperimentSettingDTO settingDTO = new ExperimentSettingDTO();
		settingDTO.setGroupId(groupUuid);
		settingDTO.setDescription("Test");
		
		
		ExperimentSetting testSetting = mService.createExperimentSetting(repo, groupUuid, settingDTO);
		
		
		Assertions.assertNotNull(testSetting);
		assertThat("ExperimentGroup should contain ExperimentSetting.", 
				expGrp.getExperimentSettings(), 
				hasItem(testSetting));
		Assertions.assertEquals(
				settingDTO.getDescription(), 
				testSetting.getDescription(), 
				"But Description was: " + testSetting.getDescription());
	}
	
	@Test
	void providingWrongIdsOrEmptyRepo_createExperimentSettingShouldReturnNull() {
		
		Repository testRepo = repoService.createNewRepository();
		
		ExperimentSetting testSetting1 = mService.createExperimentSetting(null, null, null);
		ExperimentSetting testSetting2 = mService.createExperimentSetting(testRepo, null, null);
		
		ExperimentGroup testGroup = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
		testRepo.getExperimentGroups().add(testGroup);
		String groupId = UUIDConverter.getHexFromBase64(testGroup.getId());
		ExperimentSetting testSetting3 = mService.createExperimentSetting(
				testRepo, 
				groupId, 
				null);
		
		ExperimentSettingDTO dto = new ExperimentSettingDTO();
		ExperimentSetting testSetting4 = mService.createExperimentSetting(testRepo, groupId, dto);
		
		String invalidUuid = UUID.randomUUID().toString();
		dto.setDescription("Description");
		ExperimentSetting testSetting5 = mService.createExperimentSetting(testRepo, invalidUuid, dto);
		
		Assertions.assertNull(testSetting1, "All parameters are null, should return null");
		Assertions.assertNull(testSetting2, "Empty Repository with null parameters should return null");
		Assertions.assertNull(testSetting3, "Repository with Group and valid groupId, SettingDTO is null");
		Assertions.assertNull(testSetting4, "Repository with Group and valid groupId, empty SettingsDTO");
		Assertions.assertNull(testSetting5, "Repository with Group and valid SettingsDTO, invalid GroupId");
	}
	
	@Test 
	void providingValidInput_getExperimentSettingFromExperimentGroup_ShouldReturn_ExperimentSetting() {
		
		ExperimentGroup grp = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
		ExperimentSetting setting = ExperimentDataFactory.eINSTANCE.createExperimentSetting(grp, "Test");
		grp.getExperimentSettings().add(setting);
		String validSettingId = UUIDConverter.getHexFromBase64(setting.getId());
		
		// create some random Settings
		int settingsCount = 10;
		for(int i = 0; i < settingsCount; i++) {
			ExperimentSetting temp = ExperimentDataFactory.eINSTANCE.createExperimentSetting(grp, "Test");
			grp.getExperimentSettings().add(temp);
		}
		
		ExperimentSetting testSetting = mService.getExperimentSettingFromExperimentGroup(grp, validSettingId);
		
		Assertions.assertNotNull(testSetting, "Returned ExperimentSetting is not null");
		Assertions.assertEquals(setting, testSetting, "Should be the same object");
		Assertions.assertEquals(setting.getId(), testSetting.getId(), "Ids should be the same");
		Assertions.assertEquals(setting.getDescription(), testSetting.getDescription(), "Description should be the same");
	}
	
	@Test
	void providingValidData_createExperimentRun_ShouldReturn_ExperimentRun() {
		// arrange
		Repository repo = repoService.createNewRepository();
		
		ExperimentGroup grp = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
		grp.setRepository(repo);
		
		
		ExperimentSetting setting = ExperimentDataFactory.eINSTANCE.createExperimentSetting();
		setting.setExperimentGroup(grp);
		
		String grpApiUuid = UUIDConverter.getHexFromBase64(grp.getId());
		String setApiUuid = UUIDConverter.getHexFromBase64(setting.getId());
		
		ExperimentRunDTO dto = new ExperimentRunDTO();
		dto.setDuration(1000000000);
		
		Date startTime = Date.from(Instant.now());
		dto.setStartTime(startTime);
		
		// act
		ExperimentRun testRun = mService.createExperimentRun(repo, grpApiUuid, setApiUuid, dto);
		
		// assert
		Assertions.assertNotNull(testRun, "Testrun should not be null");
		Assertions.assertEquals(setting, testRun.getExperimentSetting(), "ExperimentSetting should be set to the provided.");
		Assertions.assertEquals(grp, testRun.getExperimentSetting().getExperimentGroup(), "ExperimentGroup should");
		assertThat("ExperimentRun should be contained in ExperimentGroups-ExperimentRuns.", 
				testRun.getExperimentSetting().getExperimentRuns(), 
				hasItem(testRun));
	}
	
	@Test
	void providingValidData_createStringMeasuringPoint_ShouldReturn_StringMeasuringPoint() {
		ExperimentGroup grp = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
		
		MeasuringPointRepository mpRepo = MeasuringpointFactory.eINSTANCE.createMeasuringPointRepository();
		grp.getMeasuringPointRepositories().add(mpRepo);
		
		String description = "Testpunk 1";
		MeasuringPointDTO dto = new MeasuringPointDTO();
		dto.setMeasuringPointDescription(description);
		
		StringMeasuringPoint testSmp = mService.createStringMeasuringPoint(grp, dto);
		
		Assertions.assertNotNull(testSmp, "StringMeasuringPoint should not be null.");
		Assertions.assertEquals(description, testSmp.getMeasuringPoint());
		Assertions.assertEquals(grp.getMeasuringPointRepositories().get(0), 
				testSmp.getMeasuringPointRepository(), 
				"MeasuringPoint should reference same MeasuringPointRepository as the ExperimentGroup.");
		assertThat("StringMeasuringPoint should be contained in MeasuringPointRepository of the ExperimentGroup.",
				grp.getMeasuringPointRepositories().get(0).getMeasuringPoints(), hasItem(testSmp));

	}
	
	@Test
	void providingValidData_createMeasuringType_ShouldReturn_MeasuringType() {
		
		// arrange
		ExperimentGroup expGrp = ExperimentDataFactory.eINSTANCE.createExperimentGroup();
		expGrp.getMeasuringPointRepositories().add(
				MeasuringpointFactory.eINSTANCE.createMeasuringPointRepository());
		
		MetricDescription mDesc = TextualBaseMetricDescriptionBuilder
				.newTextualBaseMetricDescriptionBuilder()
				.name("TextName")
				.id(UUIDConverter.getBase64FromHex(UUID.randomUUID().toString()))
				.build();
		
		String measuringPointString = "Schnittstelle Test 1";
		MeasuringPoint mp = MeasuringpointFactory.eINSTANCE.createStringMeasuringPoint();
		mp.setStringRepresentation(measuringPointString);
				
		String hexExpGrpId = UUIDConverter.getHexFromBase64(expGrp.getId());
		TextualBaseMetricDescriptionDTO tbmd = DTOHelper.getTextualBaseMetricDescription((TextualBaseMetricDescription)mDesc);
		
		MeasuringTypeDTO mtDto = new MeasuringTypeDTO();
		mtDto.setExperimentGroupId(hexExpGrpId);
		mtDto.setTextualBaseMetricDescription(tbmd);
		mtDto.setMeasuringPointStringRepresentation(measuringPointString);
		
		// act
		MeasuringType mt = mService.createAndInitMeasuringType(expGrp, mtDto);
		
		// assert
		Assertions.assertNotNull(mt, "MeasuringType should not be null.");
		assertThat("The ExperimentGroup should contain the new MeasuringType.", expGrp.getMeasuringTypes(), hasItem(mt));
	}
	
	
	
	
	
	
	
	
	
	
	
}
