package org.palladiosimulator.edp2.repository.remote.server.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.Repository.Repositories;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;
import org.palladiosimulator.edp2.repository.local.LocalDirectoryRepositoryHelper;
import org.springframework.stereotype.Component;

@Component
public class RepositoriesService {

	private final String BASE_PATH = "\\Repositories\\eclipseWorkspace\\EDP2_BASE";
	
	private Repositories repos;

	@PostConstruct
	public void initRepos() throws IOException {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("edp2repos", new XMIResourceFactoryImpl());
		
		ResourceSet res = new ResourceSetImpl();
		URI repositoriesURL = URI.createFileURI("repos.edp2repos");
		Resource repositoriesRes = res.createResource(repositoriesURL);

		if (repositoriesRes.getContents().isEmpty()) {

			Repositories newRepos = RepositoryPackage.eINSTANCE.getRepositoryFactory().createRepositories();
			repositoriesRes.getContents().add(newRepos);
			repositoriesRes.save(Collections.EMPTY_MAP);
		}
		repos = (Repositories) repositoriesRes.getContents().get(0);
	}
	
	/**
	 * Return the BASE_PATH where the data should be persisted.
	 * @return Base path as String
	 */
	public String getBasePath() {
		return BASE_PATH;
	}
	
	/**
	 * Return internal Repositories collection
	 * @return
	 */
	public Repositories getRepos() {
		return repos;
	}
	
	/**
	 * Set internal repositories for testing purpose.
	 * @param repos
	 */
	public void setRepos(Repositories repos) {
		this.repos = repos;
	}
	
	public LocalDirectoryRepository getRepositoryByURI(final String uri) {
		for (Repository repo : repos.getAvailableRepositories()) {
			if(repo instanceof LocalDirectoryRepository) {
				LocalDirectoryRepository localRepo = (LocalDirectoryRepository) repo;
				if(localRepo.getUri().equals(uri))
					return localRepo;
			}
		}
		return null;
	}
	
	/**
	 * Find the repository related to the given API-ID, if it exists. Else return null.
	 * @param uuid UUID formated String
	 * @return Reference to LocalDirectoryRepository or null
	 */
	public LocalDirectoryRepository findRepositoryByApiId(final String uuid) {
		return getRepositoryByURI(getURIbyRepositoryUUID(uuid));
	}
	
	/**
	 * Creates a new LocalDirectoryRepository instance
	 * @return new Instance of LocalDirectoryRepository
	 */
	public LocalDirectoryRepository createNewRepository() {
		String repoId = UUID.randomUUID().toString();
		LocalDirectoryRepository localRepo = findOrInitRepositoryById(repoId);		
		//saveChanges();
		return localRepo;
	}
	
	/**
	 * Returns existing Repository with the given name, or initializes a new Repository.
	 * @param repoName Name of the Repository
	 * @return
	 */
	public LocalDirectoryRepository findOrInitRepositoryById(final String repoId) {
		String repoPath = URI.createFileURI(getDirPathForRepositoryId(repoId)).toString();
		return findOrInitRepository(repoPath);
	}
	
	/**
	 * Creates and initializes a new LocalRepository, if there is non existing.
	 * Returns the reference to the new or existing Repository.
	 * 
	 * @param directoryPath Directory for the Repository
	 * @return Reference to the Repository at the given directory
	 */
	public LocalDirectoryRepository findOrInitRepository(final String directoryPath) {
		
		for(Repository repo : repos.getAvailableRepositories()) {
			if(repo instanceof LocalDirectoryRepository) {
				LocalDirectoryRepository localRepo = (LocalDirectoryRepository) repo;
				if(localRepo.getUri().equals(directoryPath))
					return localRepo;
			}
		}
		return createAndInitRepository(new File(directoryPath));
	}
	
	/**
	 * Returns the ExperimentGroup with the corresponding Id contained in the Repository or null of non is found.
	 * @param repo Repository instance to be searched
	 * @param id Id of the ExperimentGroup
	 * @return ExperimentGroup with corresponding Id or null, if non is found.
	 */
	public ExperimentGroup getExperimentGroupById(final Repository repo, String id) {
		ExperimentGroup expGrp = repo.getExperimentGroups().stream()
				.filter(grp -> grp.getId().matches(id))
				.findAny()
				.orElse(null);
		return expGrp;
	}
	
	/**
	 * Returns the ExperimentSetting with the corresponding Id contained in the Repository or null, if non is found.
	 * @param repo Repository instance to be searched
	 * @param id Id of the ExperimentSetting
	 * @return ExperimentSetting with corresponding Id or null, if non is found.
	 */
	public ExperimentSetting getExperimentSettingById(final Repository repo, String id) {		
		
		for (ExperimentGroup grp : repo.getExperimentGroups()) {
			ExperimentSetting setting = grp.getExperimentSettings()
				.stream()
				.filter(set -> set.getId().matches(id))
				.findAny()
				.orElse(null);
			if(setting != null)
				return setting;
		}	
		return null;
	}
	
	/**
	 * Returns the UUID in string format for the given LocalDirectoryRepository.
	 * @param repo
	 * @return UUID formatted String
	 */
	public String getApiIdfromRepo(Repository repo) {
		String repoId = repo.getId();
		// 36 is the length of a UUID 
		return repoId.substring(repoId.length() - 36);
	}
	
	/**
	 * Returns the directory path as String for a given Repository Id, not representing 
	 * the absolute path on the file-system.
	 * @param repoId String of Repository ID
	 * @return
	 */
	public String getDirPathForRepositoryId(final String repoId) {
		return URI.createFileURI(BASE_PATH + "\\" + repoId + "\\").toString();
	}
	
	/**
	 * Returns the internal URI as generated by EDP2 for a repository, specified by the given id.
	 * @param repoId URI-String
	 * @return
	 */
	public String getURIbyRepositoryUUID(final String uuid) {
		
		String uri = new File(getDirPathForRepositoryId(uuid))
				.toURI().toString();
		uri = uri.substring(0, uri.length() - 1);
		return uri;
	}
	
	private LocalDirectoryRepository createAndInitRepository(final File dir) {
		final LocalDirectoryRepository newRepo = LocalDirectoryRepositoryHelper.initializeLocalDirectoryRepository(dir);
		RepositoryManager.addRepository(repos, newRepo);
		return newRepo;
	}	
	
	public boolean saveCurrentState() {
		try {
			repos.eResource().save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			System.err.println("[RepositoriesService]\n" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
