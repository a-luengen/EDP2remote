package org.palladiosimulator.edp2.remote;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.MeasurementDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;

@Path("/repo")
@Produces(MediaType.APPLICATION_JSON)
public interface RemoteRepositoryAPI {
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/repository")
	public Response createRepository(RepositoryInfoDTO repo);

	@GET
	@Produces("application/json")
	@Path("/repositories")
	public List<RepositoryInfoDTO> getAllRepositories();
	
	@GET
	@Path("/repository/{name}/experimentGroups")
	public List<ExperimentGroupDTO> getExperimentGroupsOfRepo(@PathParam("name") String repoName);
	
	@GET
	@Path("/repository/{name}/experimentGroups/{id}")
	public ExperimentGroupDTO getExperimentGroupFromRepo(@PathParam("name") String repoName, @PathParam("id") String expGrpId);

	@POST
	@Path("/repository/{name}/experimentGroups/{id}/experimentSetting")
	public String createExperimentSetting(@PathParam("name") String repoName, @PathParam("id") String expGrpId, ExperimentSettingDTO newSetting);
	
	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentSettings")
	public List<String> getExperimentSettings(@PathParam("name") String repoName, @PathParam("id") String expGrpId);
	
	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentSettings/{settingsId}")
	public ExperimentSettingDTO getExperimentSetting(@PathParam("name") String repoName, @PathParam("id") String expGrpId, @PathParam("settingsId") String settingsId);
	
	@POST
	@Consumes("application/json")
	@Path("/repository/{name}/experimentGroupds{id}/experimentRun")
	public String createExperimentRun(@PathParam("name") String repoName, @PathParam("id") String expGrpdId, ExperimentRunDTO newRun);
	
	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentRuns")
	public String getExperimentRuns(@PathParam("name") String repoName, @PathParam("id") String expGrpdId);
	
	@GET
	@Path("/repository/{name}/experimentGroups/{id}/experimentRuns/{expRunId}")
	public String getExperimentRun(@PathParam("name") String repoName, @PathParam("id") String expGrpdId, @PathParam("expRunId") String expRunId);
	
	@GET
	@Path("/repository/{name}/measurements")
	public String getMeasurementsOfRepo(@PathParam("name") String repoName);
	
	@GET
	@Path("/repository/{name}/measurements{measureId}/dataSeries")
	public String getDataSeriesOfMeasurement(@PathParam("name") String repoName, @PathParam("measureId") String measureId);
	
	
	
}
