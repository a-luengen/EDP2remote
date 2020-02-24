package org.palladiosimulator.edp2.remote.repository.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.repository.remote.server.service.MetaService;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestMetaService extends BasicEDP2Test {

	static MetaService mService = new MetaService();
	
	@Test
	void createNewExperimentGroup_shouldBeContainedInRepository() {
		Repository ldRepo = repoService.createNewRepository();
		
		String testPurpose = "test";
		
		ExperimentGroup grp = mService.createExperimentGroup(ldRepo, testPurpose);
		
		assertThat("New ExperimentGroup should be in Repository", ldRepo.getExperimentGroups(), hasItem(grp));
		Assertions.assertEquals(testPurpose, grp.getPurpose());
	}
	
	@Test
	void getExperimentGroupFromRepo_shouldReturnCorrectExperimentGroup() {
		Repository ldRepo = repoService.createNewRepository();
		
		ExperimentGroup grp = mService.createExperimentGroup(ldRepo, "");
		
		String grpUuid = UUIDConverter.getUuidFromBase64(grp.getId());
		
		ExperimentGroup testGrp = mService.getExperimentGroupFromRepository(ldRepo, grpUuid);
		
		Assertions.assertEquals(grp, testGrp);
	}
}
