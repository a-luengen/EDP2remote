package org.palladiosimulator.edp2.repository.remote.server.service;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.models.Repository.Repositories;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;
import org.springframework.stereotype.Component;

@Component
public class RepositoriesService {

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

	public Repositories getRepos() {
		return repos;
	}
	
	

}
