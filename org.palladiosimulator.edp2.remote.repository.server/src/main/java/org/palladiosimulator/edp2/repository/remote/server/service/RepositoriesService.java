package org.palladiosimulator.edp2.repository.remote.server.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.Repository.Repositories;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;
import org.palladiosimulator.edp2.repository.local.LocalDirectoryRepositoryHelper;
import org.springframework.stereotype.Component;

@Component
public class RepositoriesService {

	private final String BASE_PATH = "D:\\Repositories\\eclipseWorkspace\\EDP2_BASE";
	
	private Repositories repos;

	@PostConstruct
	private void initRepos() throws IOException {
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

	public String getBasePath() {
		return BASE_PATH;
	}
	
	public Repositories getRepos() {
		return repos;
	}
	
	
	/**
	 * Checks if a repository with the given path has been already created.
	 * @param path Path to check if existing repository has this path.
	 * @return
	 */
	public boolean repositoryIsExisting(final String path) {
		for(Repository rep : repos.getAvailableRepositories()) {
			if(rep instanceof LocalDirectoryRepository) {
				LocalDirectoryRepository localRepo = (LocalDirectoryRepository) rep;
				if(localRepo.getUri().equals(path))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a repository associated with the given path, or null if non exists.
	 * @param directoryPath
	 * @return Repository associated with directoryPath, or NULL if non exists.
	 */
	public LocalDirectoryRepository getRepository(final String directoryPath) {
		
		File dir = new File(directoryPath);
		String path = URI.createFileURI(dir.getAbsolutePath()).toFileString();
		
		for (Repository repo : repos.getAvailableRepositories()) {
			if(repo instanceof LocalDirectoryRepository) {
				LocalDirectoryRepository localRepo = (LocalDirectoryRepository) repo;
				if(localRepo.getUri().equals(path))
					return localRepo;
			}
		}
		return null;
	}
	
	/**
	 * Creates and initializes a new LocalRepository, if there is non existing.
	 * Returns the reference to the new or existing Repository.
	 * 
	 * @param directoryPath Directory for the Repository
	 * @return Reference to the Repository at the given directory
	 */
	public LocalDirectoryRepository findOrInitRepository(final String directoryPath) {
		File dir = new File(directoryPath);
		String path = URI.createFileURI(dir.getAbsolutePath()).toString();
		
		for(Repository repo : repos.getAvailableRepositories()) {
			if(repo instanceof LocalDirectoryRepository) {
				LocalDirectoryRepository localRepo = (LocalDirectoryRepository) repo;
				if(localRepo.getUri().equals(path))
					return localRepo;
			}
		}
		return createAndInitRepository(dir);
	}

	/**
	 * Returns the directory path as String for repoId
	 * @param repoId
	 * @return
	 */
	public String getDirPathForRepositoryId(final String repoId) {
		return URI.createFileURI(BASE_PATH + "\\" + repoId + "\\").toString();
	}
	
	private LocalDirectoryRepository createAndInitRepository(final File dir) {
		final LocalDirectoryRepository newRepo = LocalDirectoryRepositoryHelper.initializeLocalDirectoryRepository(dir);
		RepositoryManager.addRepository(repos, newRepo);
		return newRepo;
	}
}
