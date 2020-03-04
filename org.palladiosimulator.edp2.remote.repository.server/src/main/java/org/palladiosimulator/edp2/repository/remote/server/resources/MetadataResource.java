package org.palladiosimulator.edp2.repository.remote.server.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.remote.RemoteRepositoryAPI;
import org.palladiosimulator.edp2.remote.dto.DefaultConfigurationDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasurementDTO;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
	@Operation(
			summary = "Create new Repository",
			tags = {"repository"},
			description = "Creates a new Repository and returns infos about the Repository.",
			responses = {
					@ApiResponse(
								description = "The repository info", 
								content = @Content(schema = @Schema(implementation = RepositoryInfoDTO.class))
							),
					@ApiResponse(
								responseCode = "500", description = "Repository could not be created due to internal issues."
							)
			})
	@Path("/repository")
	public Response createRepository() {

		LocalDirectoryRepository localRepo = repoService.createNewRepository();
		if (localRepo == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		RepositoryInfoDTO dto = DTOHelper.getRepositoryInfoDTO(localRepo, repoService, "/edp2/meta");
		
		return Response.status(Status.CREATED).entity(dto).build();
	}

	
	
	@GET
	@Produces("application/json")
	@Consumes("application/json")
	@Operation(
				tags = {"repository"},
				summary = "Get all availables Repositories.",
				description = "Get a list of all available Respositories from the server."
			)
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
	@Operation(
			summary = "Create new ExperimentGroup.",
			tags = {"experimentGroup"},
			description = "Create a new ExperimentGroup in the Repository with the given Id.",
			responses = {
					@ApiResponse(
								responseCode = "200",
								description = "Information about the new created ExperimentGroup.",
								content = @Content(schema = @Schema(implementation = ExperimentGroupDTO.class))
							),
					@ApiResponse(
								responseCode = "404", description = "Repository not found."
							),
					@ApiResponse(
								responseCode = "500", description = "ExperimentGroup could not be created."
							)
			}
			)
	@Path("/repository/{id}/experimentGroup")
	public Response createExperimentGroup(
			@PathParam("id") String repoId, 
			@RequestBody(
					description = "Name of the ExperimentGroup.",
					required = true,
					content = @Content(
							schema = @Schema(
										implementation = ExperimentGroupDTO.class
									)
							)
					)
			String groupName) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {			
			ExperimentGroup newExperimentGroup = metaService.createExperimentGroup(localRepo, groupName);
			
			if(newExperimentGroup != null) {
				ExperimentGroupDTO expGrpDTO = DTOHelper.getExperimentGroupDTO(newExperimentGroup);
				return Response.ok().entity(expGrpDTO).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(groupName).build();
		}
		return Response.status(Status.NOT_FOUND).entity("{ repoId: \"" + repoId + "\"}").build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
				summary = "Get all ExperimentGroups in Repository",
				tags = {"repository", "experimentGroup"},
				description = "Get a list of all available ExperimentGroups from the Repository with the given Id.",
				responses = {
					@ApiResponse(
						responseCode = "200",
						description = "List of all ExperimentGroups of the Repository.",
						content = @Content(
							mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = ExperimentGroupDTO.class))
						)
					),
					@ApiResponse(
						responseCode = "404", description = "Repository not found."
					)
				}
			)
	@Path("/repository/{id}/experimentGroups")
	public Response getExperimentGroupsOfRepo(@PathParam("id") String repoId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {
			EList<ExperimentGroup> groups = localRepo.getExperimentGroups();
			
			List<ExperimentGroupDTO> resultList = new ArrayList<ExperimentGroupDTO>();
			for (ExperimentGroup expGrp : groups) {
				ExperimentGroupDTO dto = DTOHelper.getExperimentGroupDTO(expGrp);
				resultList.add(dto);
			}
			
			return Response.ok().entity(resultList).build();
		}
		return Response.status(Status.NOT_FOUND).entity("{ repoId: \"" + repoId + "\"}").build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
				summary = "Get ExperimentGroup from Repository",
				tags = {"experimentGroup", "repository"},
				description = "Get the representation of the ExperimentGroup with the given Id of the Repository with the given Id.",
				responses = {
					@ApiResponse(
						responseCode = "200",
						description = "Representation of the ExperimentGroup.",
						content = {
								@Content(
									schema = @Schema(implementation = ExperimentGroupDTO.class)
								)
						}
					),
					@ApiResponse(
							responseCode = "404"
					)
						
				}
			)
	@Path("/repository/{repoId}/experimentGroups/{id}")
	public Response getExperimentGroupFromRepo(
			@PathParam("repoId") String repoId,
			@PathParam("id") String expGrpId) {
		
		LocalDirectoryRepository repo = repoService.findRepositoryByApiId(repoId);
		if(repo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, expGrpId);
			
			if(expGrp != null)
				return Response.ok()
						.entity(DTOHelper.getExperimentGroupDTO(expGrp))
						.build();
		}
		return Response.status(Status.NOT_FOUND).entity("{ repoId: \"" + repoId + "\"}").build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Create new MeasuringPoint.",
			tags = {"experimentGroup", "measuringPoint"},
			description = "Creates a new MeasuringPoint in the ExperimentGroup with the given Id as specified in the MeasuringPointDTO.",
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "Created MeasuringPoint", 
					content = {
						@Content(
							schema = @Schema(implementation = MeasuringPointDTO.class)
						)
					}
				),
				@ApiResponse(
					responseCode = "404", description = "Repository or ExperimentGroup not found."
				),
			}
		)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/measuringType")
	public Response createMeasuringPoint(
			@PathParam("repoId") String repoId,
			@PathParam("grpId") String expGrpId, 
			@Parameter(description = "Properties of the MeasuringPoint that should be created.", required = true)
			MeasuringPointDTO mpDTO) {
		
		Repository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			ExperimentGroup grp = metaService.getExperimentGroupFromRepository(repo, expGrpId);
			
			if(grp != null) {
				StringMeasuringPoint mp = metaService.createStringMeasuringPoint(grp, mpDTO);
				mpDTO = DTOHelper.getMeasuringPointDTO(mp);
				
				return Response.ok().entity(mpDTO).build();
			}
		}
		return Response.status(Status.NOT_FOUND).entity("{ repoId: \"" + repoId + " \", grpId: \"" + expGrpId + "\"}").build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
				summary = "Create a new MeasuringType.",
				tags = {"experimentGroup", "measruingType"},
				description = "Create a new MeasuringType in the ExperimentGroup and Repository with their respective Id.",
				responses = {
					@ApiResponse(
						responseCode = "200", 
						description = "Create a new MeasuringType as described by the provided MeasringTypeDTO.",
						content = @Content(
							schema = @Schema(implementation = MeasuringTypeDTO.class)
						)
					),
					@ApiResponse( responseCode = "404", description = "ExperimentGroup or Repository not found.")
				}
			)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/measuringType")
	public Response createMeasuringType(
			@PathParam("repoId") String repoId,
			@PathParam("id") String expGrpId,
			@Parameter(description = "Properties of the MeasuringType that should be created.", required = true)
			MeasuringTypeDTO mtDTO) {
		
		Repository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, expGrpId);
			
			if(expGrp != null) {
				MeasuringType mType = metaService.createAndInitMeasuringType(expGrp, mtDTO);
				mtDTO = DTOHelper.getMeasuringTypeDTO(mType);
				if(mType != null) {
					return Response.ok(mtDTO).build();
				}
			}
		}
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId + " \", grpId: \"" + expGrpId + "\"}")
				.build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
				summary = "Create a ExperimentSetting.",
				tags = {"experimentGroup", "experimentSetting"},
				description = "Create a new ExperimentSetting in the ExperimentGroup and Repository with their respective Id.",
				responses = {
						@ApiResponse(
								responseCode = "200", 
								description = "ExperimentSetting created",
								content = @Content(
										schema = @Schema(implementation = ExperimentSettingDTO.class)
									)
							),
						@ApiResponse(responseCode = "404", description = "ExperimentGroup or Repository not found.")
				}
			)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSetting")
	public Response createExperimentSetting(
			@PathParam("repoId") String repoId, 
			@PathParam("grpId") String expGrpId,
			@Parameter(description = "Properties of the ExperimentSetting that should be created.", required = true)
			ExperimentSettingDTO newSetting) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {			
			ExperimentSetting setting = metaService.createExperimentSetting(localRepo, expGrpId, newSetting);
			
			if(setting != null)
				return Response.ok()
						.entity(DTOHelper.getExperimentSettingDTO(setting))
						.build();
		}
		return Response.status(Status.NOT_FOUND).entity("{ repoId: \"" + repoId + " \", grpId: \"" + expGrpId + "\"}").build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Get ExperimentSettings from ExperimentGroup.",
			tags = {"experimentGroup", "experimentSettings"},
			description = "Get List of available ExperimentSettings from ExperimentGroup in Repository with their respective Ids.",
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "All ExperimentSettings retrieved.",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExperimentSettingDTO.class)))
				),
				@ApiResponse(
					responseCode = "404", description = "Repository or ExperimentGroup not found."
				)
			}
		)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings")
	public Response getExperimentSettings(
			@PathParam("repoId") String repoId, 
			@PathParam("grpId") String expGrpId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(localRepo, expGrpId);
			
			if(expGrp != null) {
				List<ExperimentSettingDTO> settings = new ArrayList<ExperimentSettingDTO>();
				
				for (ExperimentSetting setting : expGrp.getExperimentSettings()) {
					settings.add(DTOHelper.getExperimentSettingDTO(setting));
				}
				return Response.ok().entity(settings).build();
			}
		}
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId + " \", grpId: \"" + expGrpId + "\"}")
				.build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
				summary = "Get ExperimentSetting",
				tags = {"experimentSetting", "experimentGroup"},
				description = "Get the ExperimentGroup with the given Id from the Repository and ExperimentGroup with their respective Ids.",
				responses = {
						@ApiResponse(
									responseCode = "200",
									description = "Response with the ExperimentSetting info.",
									content = @Content(schema = @Schema(implementation = ExperimentSettingDTO.class))
								),
						@ApiResponse(
									responseCode = "404",
									description = "Repository, ExperimentGroup or ExperimentSetting not found."
								)
				}
				
			)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}")
	public Response getExperimentSetting(
			@PathParam("repoId") String repoId,
			@PathParam("grpId") String expGrpId, 
			@PathParam("settingsId") String settingId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		if(localRepo != null) {
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(localRepo, expGrpId);
			
			if(expGrp != null) {
				
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, settingId);
				
				if(setting != null) {
					return Response.ok(DTOHelper.getExperimentSettingDTO(setting)).build();
				}
			}			
		}
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId + " \", grpId: \"" + expGrpId + ", settingsId: \"" + settingId +  "\"}")
				.build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Create new ExperimentRun.",
		tags = {"experimentSetting", "experimentRun"},
		description = "Create a new ExperimentRun in the ExperimentSetting, ExperimentGroup and Repository with their respective Id.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "ExperimentRun was created and response contains basic info",
				content = @Content(schema = @Schema(implementation = ExperimentRunDTO.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "Repository, ExperimentGroup of ExperimentSetting with the given Id was not found."
			)
		}
	)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}/experimentRun")
	public Response createExperimentRun(
			@PathParam("repoId") String repoId, 
			@PathParam("grpId") String expGrpId, 
			@PathParam("settingId") String settingId, 
			@Parameter(description = "Properties of the ExperimentRun that should be created.", required = true)
			ExperimentRunDTO newRun) {

		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {
			
			ExperimentRun run = metaService.createExperimentRun(localRepo, expGrpId, settingId, newRun);

			if(run != null) {
				return Response.ok(DTOHelper.getExperimentRunDTO(run)).build();
			}
		}		
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId + " \", grpId: \"" + expGrpId + ", settingsId: \"" + settingId +  "\"}")
				.build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all ExperimentRuns of ExperimentSetting.",
		tags = {"experimentRun", "experimentSetting"},
		description = "Get Infos for all available ExperimentRuns of the ExperimentSetting in the ExperimentGroup and Repository with their respective Id.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Returns an Array of infos for all available ExperimentRuns.",
				content = @Content(array = @ArraySchema(schema = @Schema( implementation = ExperimentRunDTO.class)))
			),
			@ApiResponse(
				responseCode = "404",
				description = "Repository, ExperimentGroup or ExperimentSetting with given Id is not found."
			)
		}
	)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}/experimentRuns")
	public Response getExperimentRuns(
			@PathParam("repoId") String repoId, 
			@PathParam("grpId") String grpId, 
			@PathParam("settingId") String settingId) {
		
		LocalDirectoryRepository localRepo = repoService.findRepositoryByApiId(repoId);
		
		if(localRepo != null) {
					
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(localRepo, grpId);
			if(expGrp != null) {
				
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, settingId);
				if(setting != null) {
					
					List<ExperimentRunDTO> dtos = new ArrayList<ExperimentRunDTO>();
					for(ExperimentRun run : setting.getExperimentRuns()) {
						dtos.add(DTOHelper.getExperimentRunDTO(run));
					}
					
					return Response.ok(dtos).build();
				}
			}
		}
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId + " \", grpId: \"" + grpId + ", settingsId: \"" + settingId +  "\"}")
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get infos of ExperimentRun.",
		tags = {"experimentRun", "experimentSetting"},
		description = "Get info of the ExperimentRun from the ExperimentSetting, ExperimentGroup and Repository with their respective Ids.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Returns infos about the ExperimentRun.",
				content = @Content(schema = @Schema( implementation = ExperimentRunDTO.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "Repository, ExperimentGroup, ExperimentSetting or ExperimentRun with respective Id is not found."
			)
		}
	)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}/experimentRuns/{runId}")
	public Response getExperimentRun(
			@PathParam("repoId") String repoId, 
			@PathParam("grpId") String grpId,
			@PathParam("settingId") String settingId,
			@PathParam("runId") String runId) {
		
		LocalDirectoryRepository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			
			ExperimentGroup expGrp = metaService.getExperimentGroupFromRepository(repo, grpId);
			if(expGrp != null) {
				
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(expGrp, settingId);
				if(setting != null) {
					
					ExperimentRun run = metaService.getExperimentRunFromExperimentSetting(setting, runId);
					if(run != null)
						return Response.ok(DTOHelper.getExperimentRunDTO(run)).build();
				}
			}

		}
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId 
						+ " \", grpId: \"" + grpId 
						+ "\", settingsId: \"" + settingId 
						+  "\", runId: \"" + runId + "\"}")
				.build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			
				
			)
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}/experimentRuns/{runId}/measurement")
	public Response createMeasurement(
			@PathParam("repoId") String repoId, 
			@PathParam("grpId") String grpId,
			@PathParam("settingId") String settingId,
			@PathParam("runId") String runId,
			MeasurementDTO mDto) {
		
		Repository repo = repoService.findRepositoryByApiId(repoId);
		
		if(repo != null) {
			ExperimentGroup grp = metaService.getExperimentGroupFromRepository(repo, grpId);
			
			if(grp != null) {
				ExperimentSetting setting = metaService.getExperimentSettingFromExperimentGroup(grp, settingId);
				
				if(setting != null) {
					ExperimentRun run = metaService.getExperimentRunFromExperimentSetting(setting, runId);
					
					if(run != null) {
						Measurement m = metaService.createAndInitMeasurement(run, mDto);
						
						if(m != null) {
							return Response.ok(DTOHelper.getMeasurementDTO(m)).build();
						}
					}
				}
			}
		}
		return Response.status(Status.NOT_FOUND)
				.entity("{ repoId: \"" + repoId 
						+ " \", grpId: \"" + grpId 
						+ "\", settingsId: \"" + settingId 
						+  "\", runId: \"" + runId + "\"}")
				.build();
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
