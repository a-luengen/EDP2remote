package org.palladiosimulator.edp2.remote.repository.service;

import java.io.File;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.e4.ui.css.swt.helpers.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.Repository.Repositories;


public class TestRepositoriesService extends BasicEDP2Test {

	@Test
	void testCorrectGetBasePath() {
		String expectedBasePath = "\\Repositories\\eclipseWorkspace\\EDP2_BASE";
		
		String basePath = repoService.getBasePath();
		
		Assertions.assertEquals(expectedBasePath, basePath);
	}
	
	@Test
	void providingValidLocalRepository_returnsUUIDContainedInID() {
		LocalDirectoryRepository repo = repoService.createNewRepository();
		String uuid = repoService.getApiIdfromRepo(repo);
		
		Assertions.assertTrue(Pattern.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", uuid), uuid);
		Assertions.assertTrue(repo.getId().contains(uuid), uuid);
	}
	
	@Test
	void createRepository_ShouldNotReturnNull() {
		LocalDirectoryRepository repo = repoService.createNewRepository();
		Assertions.assertNotNull(repo);
	}
	
	@Test
	void createRepository_RepoIsPresentInRepositories() {
		LocalDirectoryRepository repo = repoService.createNewRepository();
		
		Repositories repos = repoService.getRepos();
		
		assertThat("New Repository should be in Repositories", repos.getAvailableRepositories(), hasItem(repo));
	}
	
	@Test
	void getDirPathForRepositoryId_returnsCorrectString() {
		String id = UUID.randomUUID().toString();
		String dirPath = URI.createFileURI(repoService.getBasePath() + "\\" + id + "\\").toString();

		
		String dirPathToTest = repoService.getDirPathForRepositoryId(id);
		
		Assertions.assertNotNull(dirPathToTest);
		Assertions.assertEquals(dirPath, dirPathToTest, dirPathToTest);		
	}
	
	@Test
	void getAbsoluteDirPathForRepositoryId_returnsCorrectString() {
		LocalDirectoryRepository repo = repoService.createNewRepository();
		String id = repoService.getApiIdfromRepo(repo);
		
		String absolutePathToTest = new File(repoService.getDirPathForRepositoryId(id)).toURI().toString();
		absolutePathToTest = absolutePathToTest.substring(0, absolutePathToTest.length() - 1);

		Assertions.assertNotNull(absolutePathToTest);
		Assertions.assertNotNull(repo);
		Assertions.assertTrue(repo.getId().contains(absolutePathToTest), absolutePathToTest);
		Assertions.assertEquals(repo.getId(), absolutePathToTest);
	}
	
	@Test
	void getRepositoryById_shoudlReturnCorrectRepository() {
		LocalDirectoryRepository repo1 = repoService.createNewRepository();
		repoService.createNewRepository();
		repoService.createNewRepository();
		
		LocalDirectoryRepository testResult = repoService.findRepositoryByApiId(repoService.getApiIdfromRepo(repo1));
		
		Assertions.assertNotNull(repo1, "Expected a not-null object reference");
		Assertions.assertNotNull(testResult, "Expected reference to repo1 in Object.");
		Assertions.assertEquals(repo1, testResult, "Expected repo1: <" + repo1.getId() + ">");
	}
}
