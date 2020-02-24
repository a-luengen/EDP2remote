package org.palladiosimulator.edp2.repository.remote.server.service;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;
import org.springframework.stereotype.Component;

@Component
public class MetaService {

	private final static ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;

	
	public ExperimentGroup createExperimentGroup(Repository repo, String groupName) {
		ExperimentGroup expGrp = EXPERIMENT_DATA_FACTORY.createExperimentGroup();
		
		expGrp.setPurpose(groupName);
		expGrp.setRepository(repo);
		
		return expGrp;
	}
	
	public ExperimentGroup getExperimentGroupFromRepository(Repository repo, String expGrpId) {
		
		String internalId = UUIDConverter.getBase64FromUuid(expGrpId);
		
		return repo.getExperimentGroups()
				.stream()
				.filter(grp -> grp.getId().equals(internalId))
				.findAny()
				.orElse(null);
	}
}
