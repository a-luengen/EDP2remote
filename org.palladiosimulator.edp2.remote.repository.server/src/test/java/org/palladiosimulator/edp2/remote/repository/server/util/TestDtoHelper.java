package org.palladiosimulator.edp2.remote.repository.server.util;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;
import org.palladiosimulator.edp2.repository.remote.server.util.DTOMapper;

public class TestDtoHelper {

	public static RepositoriesService rs;
	
	@BeforeAll
	public static void init() throws IOException {
		rs = new RepositoriesService();
		rs.initRepos();
	}
	
	@Test
	void createRepositoryDTO_shouldNotThrowException() throws IOException {
		
		Assertions.assertNotNull(rs);
		Repository repo = rs.createNewRepository();
		
		Assertions.assertNotNull(repo, "Repository should not be null.");
		Assertions.assertNotNull(rs, "RepositoriesService should not be null");
		Assertions.assertNotNull(repo.getId(), "Id of repo should not be null.");
		
		RepositoryInfoDTO dto = DTOMapper.getRepositoryInfoDTO(repo, rs, "/test/");
		
		Assertions.assertNotNull(dto, "DTO should not be null.");
	}
	
}
