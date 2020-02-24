package org.palladiosimulator.edp2.remote.repository.service;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;

public class BasicEDP2Test {
	
	public static RepositoriesService repoService;
	
	@BeforeAll
	static void setup() throws IOException {
		repoService = new RepositoriesService();
		repoService.initRepos();
	}
	
	@AfterAll
	static void removeFiles( ) {
		// remove content in base directory
		File baseDirectory = new File(repoService.getBasePath());
		
		if(baseDirectory.exists() && baseDirectory.isDirectory()) {
			for (File repoFile : baseDirectory.listFiles()) {
				if(!repoFile.delete()) {
					System.err.println("[TestRepositoriesService] - File couldn't be deleted <" 
							+ repoFile.getAbsolutePath()
							+ ">");
				}
			}
		}
		
		// remove repositories-persistance file
	}
}
