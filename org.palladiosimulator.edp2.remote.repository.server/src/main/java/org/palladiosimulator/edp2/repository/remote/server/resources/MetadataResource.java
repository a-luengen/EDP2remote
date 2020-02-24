package org.palladiosimulator.edp2.repository.remote.server.resources;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.palladiosimulator.edp2.dao.*;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.remote.RemoteRepositoryAPI;
import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasurementDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;
import org.palladiosimulator.edp2.repository.local.LocalDirectoryRepositoryHelper;
import org.palladiosimulator.edp2.repository.local.dao.FileBinaryMeasurementsDaoImpl;
import org.palladiosimulator.edp2.repository.local.dao.LocalDirectoryMeasurementsDaoFactory;
import org.palladiosimulator.edp2.repository.remote.server.service.MetaService;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;
import org.palladiosimulator.edp2.repository.remote.server.util.DTOHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@Path("/meta/")
public class MetadataResource implements RemoteRepositoryAPI {

	private static String BASE_PATH = "D:\\Repositories\\eclipseWorkspace\\EDP2_BASE";

	private final static ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
	private final static MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;
	
	@Autowired
	private RepositoriesService repoService;
	
	@Autowired
	private MetaService metaService;
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/repository")
	public Response createRepository() {

		LocalDirectoryRepository localRepo = repoService.createNewRepository();
		if (localRepo == null) {
			return Response.serverError().build();
		}
		
		RepositoryInfoDTO dto = DTOHelper.getRepositoryInfoDTO(localRepo, repoService, "/edp2/meta");
		
		return Response.status(Status.CREATED).entity(dto).build();
	}

	@GET
	@Produces("application/json")
	@Path("/repositories")
	public List<RepositoryInfoDTO> getAllRepositories() {
		EList<Repository> repos = repoService.getRepos().getAvailableRepositories();

		List<RepositoryInfoDTO> result = new ArrayList<>();
		for (Repository repository : repos) {
			RepositoryInfoDTO dto = DTOHelper.getRepositoryInfoDTO(repository, repoService, "/edp2/meta");			
			result.add(dto);
		}
		return result;
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/repository/{id}/experimentGroup")
	public Response createExperimentGroup(@PathParam("id") String repoId, String groupName) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {			
			ExperimentGroup newExperimentGroup = metaService.createExperimentGroup(localRepo, groupName);
			
			if(newExperimentGroup != null) {
				
				// create the DTO
				ExperimentGroupDTO expGrpDTO = DTOHelper.getExperimentGroupDTO(newExperimentGroup);
				
				return Response.ok().entity(expGrpDTO).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(groupName).build();
		}
		return Response.status(Status.NOT_ACCEPTABLE).entity(repoId).build();
	}
	
	@GET
	@Path("/repository/{id}/experimentGroups")
	public List<ExperimentGroupDTO> getExperimentGroupsOfRepo(@PathParam("id") String repoId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {
			EList<ExperimentGroup> groups = localRepo.getExperimentGroups();
			
			
			List<ExperimentGroupDTO> resultList = new ArrayList<ExperimentGroupDTO>();
			for (ExperimentGroup expGrp : groups) {
				ExperimentGroupDTO dto = DTOHelper.getExperimentGroupDTO(expGrp);
				resultList.add(dto);
			}
			return resultList;
		}
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{id}")
	public ExperimentGroupDTO getExperimentGroupFromRepo(@PathParam("repoId") String repoId,
			@PathParam("id") String expGrpId) {
		
		LocalDirectoryRepository repo = repoService.findRepositoryByApiId(repoId);
		if(repo != null) {
			for (ExperimentGroup expGrp : repo.getExperimentGroups()) {
				if(expGrp.getId().contentEquals(expGrpId))
					return DTOHelper.getExperimentGroupDTO(expGrp);
			}	
		}
		return null;
	}

	@POST
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSetting")
	public String createExperimentSetting(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId,
			ExperimentSettingDTO newSetting) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			// get experiment group
			ExperimentGroup expGrp = repoService.getExperimentGroupById(localRepo, expGrpId);
			
			if(expGrp != null) {
				ExperimentSetting setting = EXPERIMENT_DATA_FACTORY.createExperimentSetting(expGrp, newSetting.getDescription());
				if(setting != null) {
					return "/edp2/meta/repository/" + repoId + "/experimentGroups/" + expGrpId + "/experimentSetting/" + setting.getId();
				}
			}
		}
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings")
	public List<String> getExperimentSettings(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			ExperimentGroup expGrp = repoService.getExperimentGroupById(localRepo, expGrpId);
			
			List<String> settingIds = new ArrayList<String>();
			
			for (ExperimentSetting setting : expGrp.getExperimentSettings()) {
				settingIds.add(setting.getId());
			}
			return settingIds;
		}
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}")
	public ExperimentSettingDTO getExperimentSetting(@PathParam("repoId") String repoId,
			@PathParam("grpId") String expGrpId, @PathParam("settingsId") String settingsId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			ExperimentGroup expGrp = repoService.getExperimentGroupById(localRepo, expGrpId);
			
			if(expGrp != null) {
				
				ExperimentSetting setting = expGrp.getExperimentSettings()
						.stream()
						.filter(set -> set.getId().matches(settingsId))
						.findAny()
						.orElse(null);
				
				if(setting != null) {
					return DTOHelper.getExperimentSettingDTO(setting);
				}
			}			
		}
		
		return null;
	}

	@POST
	@Consumes("application/json")
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}/experimentRun")
	public String createExperimentRun(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId, 
			@PathParam("settingId") String settingId, ExperimentRunDTO newRun) {

		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {
			ExperimentGroup expGrp = repoService.getExperimentGroupById(localRepo, expGrpId);
			
			if(expGrp != null) {
				ExperimentSetting setting = expGrp.getExperimentSettings()
						.stream()
						.filter(set -> set.getId().matches(expGrpId))
						.findAny()
						.orElse(null);
				
				if(setting != null) {
					ExperimentRun run = EXPERIMENT_DATA_FACTORY.createExperimentRun(setting);	
					
					if(run != null) {
						return "/repository/" + repoId 
								+ "/experimentGroups/" + expGrpId 
								+ "/experimentSettings/" + settingId 
								+ "/experimentRun/" + run.getId();
					}
				}
			}
		}		
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}/experimentRuns")
	public List<ExperimentRunDTO> getExperimentRuns(@PathParam("repoId") String repoId, @PathParam("grpId") String grpId, 
			@PathParam("settingsId") String settingsId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {
			ExperimentGroup expGrp = repoService.getExperimentGroupById(localRepo, grpId);
			
			if(expGrp != null) {
				// get experiment Setting
				
				ExperimentSetting setting = expGrp.getExperimentSettings()
						.stream()
						.filter(set -> set.getId().matches(settingsId))
						.findAny()
						.orElse(null);
				if(setting != null) {
					// get the experimentRuns
					
					List<ExperimentRunDTO> dtos = new ArrayList<ExperimentRunDTO>();
					for(ExperimentRun run : setting.getExperimentRuns()) {
						dtos.add(DTOHelper.getExperimentRunDTO(run));
					}
					
					return dtos;
				}
			}
		}
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}/experimentRuns/{runId}")
	public ExperimentRunDTO getExperimentRun(@PathParam("repoId") String repoId, @PathParam("grpId") String grpId,
			@PathParam("settingsId") String settingsId,
			@PathParam("runId") String runId) {
		
		LocalDirectoryRepository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			ExperimentSetting setting = repoService.getExperimentSettingById(repo, settingsId);
			
			if(setting != null) {
				ExperimentRun run = setting.getExperimentRuns()
						.stream()
						.filter(r -> r.getId().matches(runId))
						.findAny()
						.orElse(null);
				
				if(run != null)
					return DTOHelper.getExperimentRunDTO(run);
			}
		}
		return null;
	}

	/**
	 * Return all the MeasuringPoints of the MeasuringPointRepository of the given
	 * experiment Group
	 * 
	 * @return Me0asuringPoints of the
	 */
	@GET
	@Path("/repository/{name}/experimentGroups/{id}")
	public String getMeasuringPoints() {
		return null;
	}

	/*
	 * Endpoints for DataSeries from a Repository
	 */

	@GET
	@Path("/repository/{name}/measurements")
	public String getMeasurementsOfRepo(@PathParam("name") String repoName) {
		return null;
	}

	@GET
	@Path("/repository/{name}/measurements{measureId}/dataSeries")
	public String getDataSeriesOfMeasurement(@PathParam("name") String repoName,
			@PathParam("measureId") String measureId) {
		return null;
	}

	@POST
	@Path("/dataseries")
	public String uploadRawDataSeries(RepositoryInfoDTO repo) {

		URI directory = URI.createFileURI(BASE_PATH + "\\" + repo.getName());

		// use path of RepositoryService to find the MeasuremntsDaoFactory
		MeasurementsDaoFactory factory = LocalDirectoryMeasurementsDaoFactory.getRegisteredFactory(directory);
		
		factory.createLongMeasurementsDao(null);
		
		// MeasurementsDao<> mDao = factory.createDoubleMeasurementsDao();
		return null;
	}

}
