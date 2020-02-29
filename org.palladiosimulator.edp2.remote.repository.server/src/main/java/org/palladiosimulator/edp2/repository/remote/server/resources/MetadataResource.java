package org.palladiosimulator.edp2.repository.remote.server.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.remote.RemoteRepositoryAPI;
import org.palladiosimulator.edp2.remote.dto.DefaultConfigurationDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringPointDTO;
import org.palladiosimulator.edp2.remote.dto.MeasuringTypeDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;
import org.palladiosimulator.edp2.repository.local.dao.LocalDirectoryMeasurementsDaoFactory;
import org.palladiosimulator.edp2.repository.remote.server.service.DataseriesService;
import org.palladiosimulator.edp2.repository.remote.server.service.MetaService;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;
import org.palladiosimulator.edp2.repository.remote.server.util.DTOHelper;
import org.palladiosimulator.edp2.repository.remote.server.util.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/meta/")
public class MetadataResource implements RemoteRepositoryAPI {

	private static String BASE_PATH = "D:\\Repositories\\eclipseWorkspace\\EDP2_BASE";

	private final static MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;
	
	@Autowired
	private RepositoriesService repoService;
	
	@Autowired
	private MetaService metaService;
	
	@Autowired
	private DataseriesService dataService;
	
	public MetadataResource(RepositoriesService rs, MetaService ms, DataseriesService ds) {
		this.repoService = rs;
		this.metaService = ms;
		this.dataService = ds;
	}
	
	
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
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, expGrpId);
			
			if(expGrp != null)
				return DTOHelper.getExperimentGroupDTO(expGrp);
		}
		return null;
	}

	@POST
	@Path("/repository/{repoId}/experimentGroups/{grpId}/measuringType")
	public Response createMeasuringPoint(@PathParam("repoId") String repoId,
			@PathParam("id") String expGrpId, MeasuringPointDTO mpDTO) {
		
		Repository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			ExperimentGroup grp = metaService.getExperimentGroupFromRepository(repo, expGrpId);
			
			if(grp != null) {
				StringMeasuringPoint mp = metaService.createStringMeasuringPoint(grp, mpDTO);
				mpDTO = DTOHelper.getMeasuringPointDTO(mp);
				
				return Response.accepted().entity(mpDTO).build();
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	@POST
	@Path("/repository/{repoId}/experimentGroups/{grpId}/measuringType")
	public Response createMeasuringType(@PathParam("repoId") String repoId,
			@PathParam("id") String expGrpId, MeasuringTypeDTO mtDTO) {
		
		Repository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, expGrpId);
			
			if(expGrp != null) {
				MeasuringType mType = metaService.createMeasuringType(expGrp, mtDTO);
				
				if(mType != null) {
					return Response.accepted(mtDTO).build();
				}
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
			ExperimentSetting setting = metaService.createExperimentSetting(localRepo, expGrpId, newSetting);
			
			if(setting != null)
				return "/edp2/meta/repository/" + repoId 
						+ "/experimentGroups/" 	+ expGrpId 
						+ "/experimentSetting/" + UUIDConverter.getUuidFromBase64(setting.getId());
		}
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings")
	public List<String> getExperimentSettings(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(localRepo, expGrpId);
			
			if(expGrp != null) {
				List<String> settingIds = new ArrayList<String>();
				
				for (ExperimentSetting setting : expGrp.getExperimentSettings()) {
					settingIds.add(setting.getId());
				}
				return settingIds;
			}
		}
		return null;
	}

	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}")
	public ExperimentSettingDTO getExperimentSetting(@PathParam("repoId") String repoId,
			@PathParam("grpId") String expGrpId, @PathParam("settingsId") String settingsId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(localRepo, expGrpId);
			
			if(expGrp != null) {
				
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, settingsId);
				
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
			
			ExperimentRun run = metaService.createExperimentRun(localRepo, expGrpId, settingId, newRun);

			if(run != null) {
				return "/repository/" + repoId 
						+ "/experimentGroups/" + expGrpId 
						+ "/experimentSettings/" + settingId 
						+ "/experimentRun/" + UUIDConverter.getUuidFromBase64(run.getId());
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
					
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(localRepo, grpId);
			if(expGrp != null) {
				
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, settingsId);
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
			
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, grpId);
			if(expGrp != null) {
				
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, settingsId);
				if(setting != null) {
					
					ExperimentRun run = metaService.getExperimentRunFromExperimentSetting(setting, runId);
					if(run != null)
						return DTOHelper.getExperimentRunDTO(run);
				}
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

		ExperimentDataFactory FACT = ExperimentDataFactory.eINSTANCE;
		
		FACT.createDoubleBinaryMeasurements();
		
		URI directory = URI.createFileURI(BASE_PATH + "\\" + repo.getName());

		// use path of RepositoryService to find the MeasuremntsDaoFactory
		MeasurementsDaoFactory factory = LocalDirectoryMeasurementsDaoFactory.getRegisteredFactory(directory);
		
		
		// MeasurementsDao<> mDao = factory.createDoubleMeasurementsDao();
		return null;
	}
	
	@POST
	@Path("/dataseries/default")
	public Response createDefaultExperimentSetup(DefaultConfigurationDTO config) {
		
		// 1. Get the Run
		Repository repo = repoService.findRepositoryByApiId(config.getRepoId());
		
		if(repo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, config.getGroupId());
			if(expGrp != null) {
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, config.getGroupId());
				if(setting != null) {
					ExperimentRun run = metaService.getExperimentRunFromExperimentSetting(setting, config.getRunId());
					
					// 2. create default setting to store rawMeasurement
					
					dataService.prepareDefaultSetup(run, config);
					
				}
			}
		}
		
		
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@POST
	@Path("/dataseries/double")
	public Response addDoubleDataSeries() {
		
		// 1. Get the Run
		// 2. Get the Measurement
		// 3. Get Range
		// 4. Add through rawMeasurements the Dataseries
		
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}

}
