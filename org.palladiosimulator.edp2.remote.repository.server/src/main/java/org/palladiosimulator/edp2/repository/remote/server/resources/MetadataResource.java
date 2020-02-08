package org.palladiosimulator.edp2.repository.remote.server.resources;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.FileDataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.palladiosimulator.edp2.dao.*;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.remote.RemoteRepositoryAPI;
import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasurementDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;
import org.palladiosimulator.edp2.repository.local.LocalDirectoryRepositoryHelper;
import org.palladiosimulator.edp2.repository.local.dao.FileBinaryMeasurementsDaoImpl;
import org.palladiosimulator.edp2.repository.local.dao.LocalDirectoryMeasurementsDaoFactory;
import org.palladiosimulator.edp2.repository.remote.server.service.RepositoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/meta/")
public class MetadataResource implements RemoteRepositoryAPI {

	private Map<String, RepositoryInfoDTO> repositories = new HashMap<String, RepositoryInfoDTO>();
	private static String BASE_PATH = "D:\\Repositories\\eclipseWorkspace\\EDP2_BASE";

	private final static ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
	
	@Autowired
	private RepositoriesService repoService;

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/repository")
	public Response createRepository(RepositoryInfoDTO repo) {

		if (repo != null) {

			String repoPath = URI.createFileURI(BASE_PATH + "\\" + repo.getId() +"\\").toString();
			LocalDirectoryRepository localRepo = repoService.findOrInitRepository(repoPath);

			if (localRepo == null) {
				return Response.serverError().build();
			}
			return Response.created(UriBuilder.fromPath("/repository/" + localRepo.getUri()).build()).build();
		}
		return Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}

	@GET
	@Produces("application/json")
	@Path("/repositories")
	public List<RepositoryInfoDTO> getAllRepositories() {
		EList<Repository> repos = repoService.getRepos().getAvailableRepositories();

		List<RepositoryInfoDTO> result = new ArrayList<>();
		for (Repository repository : repos) {
			RepositoryInfoDTO dto = new RepositoryInfoDTO();
			dto.setId(repository.getId());
			result.add(dto);
		}
		return result;
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/repository/{id}/experimentGroup")
	public Response createExperimentGroup(@PathParam("id") String repoId, String groupName) {
		
		String repoPath = URI.createFileURI(BASE_PATH + "\\" + repoId + "\\").toString();
		
		LocalDirectoryRepository localRepo = repoService.getRepository(repoPath);
		
		if(localRepo != null) {			
			ExperimentGroup newExperimentGroup = EXPERIMENT_DATA_FACTORY.createExperimentGroup();
			
			if(newExperimentGroup != null) {
				newExperimentGroup.setPurpose(groupName);
				newExperimentGroup.setRepository(localRepo);
				
				// create the DTO
				ExperimentGroupDTO expGrpDTO = new ExperimentGroupDTO();
				expGrpDTO.setUuid(newExperimentGroup.getId());
				expGrpDTO.setPurpose(groupName);
				
				return Response.ok().entity(expGrpDTO).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(groupName).build();
		}
		return Response.status(Status.NOT_ACCEPTABLE).entity(repoId).build();
	}
	
	@GET
	@Path("/repository/{id}/experimentGroups")
	public List<ExperimentGroupDTO> getExperimentGroupsOfRepo(@PathParam("id") String repoId) {
		String repoPath = repoService.getDirPathForRepositoryId(repoId);
		LocalDirectoryRepository localRepo = repoService.getRepository(repoPath);
		
		EList<ExperimentGroup> groups = localRepo.getExperimentGroups();
		
		
		List<ExperimentGroupDTO> resultList = new ArrayList<ExperimentGroupDTO>();
		
		for (ExperimentGroup expGrp : groups) {
			ExperimentGroupDTO dto = new ExperimentGroupDTO();
			dto.setUuid(expGrp.getId());
			dto.setPurpose(expGrp.getPurpose());
		}
		
		return null;
	}

	@GET
	@Path("/repository/{name}/experimentGroups/{id}")
	public ExperimentGroupDTO getExperimentGroupFromRepo(@PathParam("name") String repoName,
			@PathParam("id") String expGrpId) {
		return null;
	}

	@POST
	@Path("/repository/{name}/experimentGroups/{id}/experimentSetting")
	public String createExperimentSetting(@PathParam("name") String repoName, @PathParam("id") String expGrpId,
			ExperimentSettingDTO newSetting) {
		return null;
	}

	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentSettings")
	public List<String> getExperimentSettings(@PathParam("name") String repoName, @PathParam("id") String expGrpId) {
		return null;
	}

	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentSettings/{settingsId}")
	public ExperimentSettingDTO getExperimentSetting(@PathParam("name") String repoName,
			@PathParam("id") String expGrpId, @PathParam("settingsId") String settingsId) {
		return null;
	}

	@POST
	@Consumes("application/json")
	@Path("/repository/{name}/experimentGroupds{id}/experimentRun")
	public String createExperimentRun(@PathParam("name") String repoName, @PathParam("id") String expGrpdId,
			ExperimentRunDTO newRun) {

		return null;
	}

	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentRuns")
	public String getExperimentRuns(@PathParam("name") String repoName, @PathParam("id") String expGrpdId) {
		return null;
	}

	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentRuns/{expRunId}")
	public String getExperimentRun(@PathParam("name") String repoName, @PathParam("id") String expGrpdId,
			@PathParam("expRunId") String expRunId) {
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

	@POST
	@Path("/repository/{name}/uploaddataseries")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response storeDataSeriesBinary(@PathParam("name") String repoName, MultipartFormDataInput input) {

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");

		String fileName = "";
		List<String> ids = new ArrayList<String>();
		for (InputPart part : inputParts) {

			try {

				InputStream is = part.getBody(InputStream.class, null);
				byte[] rawData = IOUtils.toByteArray(is);
				// String tempId = uuidGenerator.generateRandomUUID();
				String tempId = "";
				ids.add(tempId);
				// add repoName,
				fileName = BASE_PATH + "\\repoName\\" + tempId + ".edp2.bin";

				File file = new File(fileName);

				if (!file.exists()) {
					file.createNewFile();
				}

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(rawData);
				fos.flush();
				fos.close();

			} catch (IOException e) {
				e.printStackTrace();
				return Response.status(500).entity(e.getMessage()).build();
			}
		}
		return Response.ok(200).entity(ids).build();
	}

	@POST
	@Path("/repository/{name}/downloaddataseries/{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadDataseries(@PathParam("name") String repoName, @PathParam("uuid") String uuid) {
		if (repoName == null || repoName.isEmpty() || uuid == null || uuid.isEmpty()) {
			throw new WebApplicationException(404);
		}
		// correct name is
		String filePath = BASE_PATH + "\\" + repoName + "\\" + "dataSeries" + "\\" + uuid;
		File file = new File(filePath);
		try {

			if (!file.exists())
				throw new WebApplicationException(404);

			FileInputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);

			byte[] rawData = bis.readAllBytes();

			bis.close();
			is.close();

		} catch (IOException e) {
			throw new WebApplicationException(e.getMessage(), 500);
		}

		// return Response.ok(200).entity(rawData).build();
		return null;
	}

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

		MeasurementsDaoFactory factory = LocalDirectoryMeasurementsDaoFactory.getRegisteredFactory(directory);

		factory.createLongMeasurementsDao(null);

		// MeasurementsDao<> mDao = factory.createDoubleMeasurementsDao();
		return null;
	}

}
