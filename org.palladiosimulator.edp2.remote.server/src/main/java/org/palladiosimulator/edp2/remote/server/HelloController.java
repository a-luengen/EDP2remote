package org.palladiosimulator.edp2.remote.server;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.repository.local.LocalDirectoryRepositoryHelper;

@RestController
public class HelloController {
	
	private final static ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
	private final static MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;

	@RequestMapping("/repo")
	public List<String> listRepositories() {
		
		List<String> result = new ArrayList<>();
		 for(Repository rep : RepositoryManager.getCentralRepository().getAvailableRepositories()){	
			 result.add(rep.getId());
		 }
		 
		return result;
	}
	
	@RequestMapping("/repo/{repoId}/")
	public String getRepository(@PathVariable String repoId) {
		
		LocalDirectoryRepository repo = findOrInitRepository(repoId);
		return repo.getId();
	}

	
	@RequestMapping("/repo/{repoId}/group")
	public List<ExperimentGroup> getGroups(@PathVariable String repoId) {
		
		LocalDirectoryRepository repo = findOrInitRepository(repoId);
		EList<ExperimentGroup> groups = repo.getExperimentGroups();
		
		//TODO: only for testing
		if(groups.isEmpty()) {
			ExperimentGroup defaultGroup = EXPERIMENT_DATA_FACTORY.createExperimentGroup();
			defaultGroup.setPurpose("default");
			groups.add(defaultGroup);
		}
		
		return groups;
	}
	
	@PostMapping("/repo/{repoId}/group")
	public ResponseEntity<ExperimentGroup> create(@PathVariable String repoId, @RequestBody String groupName) throws URISyntaxException {
		
		LocalDirectoryRepository repo = findOrInitRepository(repoId);
		ExperimentGroup createdExperimentGroup = EXPERIMENT_DATA_FACTORY.createExperimentGroup();

		if (createdExperimentGroup == null) {
	        return ResponseEntity.notFound().build();
	    } else {

	    	createdExperimentGroup.setPurpose(groupName);
	    	createdExperimentGroup.setRepository(repo);
	        
	    	java.net.URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
	          .path("/{id}")
	          .buildAndExpand(createdExperimentGroup.getId())
	          .toUri();
	 
	        return ResponseEntity.created(uri)
	          .body(createdExperimentGroup);
	    }
	}

	
	
	
	/**
     * Initializes the repository in which the data will be stored.
     * 
     * @param directory
     *            Path to directory in which the data should be stored.
     * @return the initialized repository.
     */
    private LocalDirectoryRepository findOrInitRepository(final String directory) {
    	File dir = new File(directory);
        
        /*
         * Add repository to a (optional) central directory of repositories. This can be useful to
         * manage more than one repository or have links between different existing repositories. A
         * repository must be connected to an instance of Repositories in order to be opened.
         */
        
        String path = URI.createFileURI(dir.getAbsolutePath()).toString();
        
        for(Repository rep : RepositoryManager.getCentralRepository().getAvailableRepositories()){
        	if(rep instanceof LocalDirectoryRepository){
        		LocalDirectoryRepository ldr = (LocalDirectoryRepository)rep;
        		if(ldr.getUri().equals(path)){
        			return ldr;
        		}
        	}
        }
        
        //if the repository for the specified path is not found
        final LocalDirectoryRepository repo = LocalDirectoryRepositoryHelper.initializeLocalDirectoryRepository(dir);
        RepositoryManager.addRepository(RepositoryManager.getCentralRepository(), repo);
        return repo;
    }

}