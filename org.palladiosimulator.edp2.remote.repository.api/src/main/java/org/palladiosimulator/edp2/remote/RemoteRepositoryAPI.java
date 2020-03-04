package org.palladiosimulator.edp2.remote;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.palladiosimulator.edp2.remote.dto.ExperimentGroupDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentRunDTO;
import org.palladiosimulator.edp2.remote.dto.ExperimentSettingDTO;
import org.palladiosimulator.edp2.remote.dto.RepositoryInfoDTO;

@Path("/repo")
@Produces(MediaType.APPLICATION_JSON)
public interface RemoteRepositoryAPI {
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/repository")
	public Response createRepository();

	@GET
	@Produces("application/json")
	@Path("/repositories")
	public List<RepositoryInfoDTO> getAllRepositories();
	
	@GET
	@Path("/repository/{repoId}/experimentGroups")
	public Response getExperimentGroupsOfRepo(@PathParam("repoId") String repoId);
	
	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}")
	public Response getExperimentGroupFromRepo(@PathParam("repoId") String repoName, @PathParam("grpId") String expGrpId);

	@POST
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSetting")
	public Response createExperimentSetting(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId, ExperimentSettingDTO newSetting);
	
	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings")
	public Response getExperimentSettings(@PathParam("repoId") String repoName, @PathParam("grpId") String expGrpId);
	
	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}")
	public Response getExperimentSetting(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId, @PathParam("settingsId") String settingsId);
	
	@POST
	@Consumes("application/json")
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}/experimentRun")
	public Response createExperimentRun(@PathParam("repoId") String repoId, @PathParam("grpId") String expGrpId, @PathParam("settingId") String settingId, ExperimentRunDTO newRun);
	
	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingId}/experimentRuns")
	public Response getExperimentRuns(@PathParam("repoId") String repoId, @PathParam("grpId") String grpId, @PathParam("settingId") String settingId);
	
	@GET
	@Path("/repository/{repoId}/experimentGroups/{grpId}/experimentSettings/{settingsId}/experimentRuns/{runId}")
	public Response getExperimentRun(@PathParam("repoId") String repoId, @PathParam("grpId") String grpId, @PathParam("settingsId") String settingsId, @PathParam("runId") String runId);	
	
	@GET
	@Path("/repository/{repoId}/measurements")
	public String getMeasurementsOfRepo(@PathParam("repoId") String repoName);
	
	@GET
	@Path("/repository/{repoId}/measurements{measureId}/dataSeries")
	public String getDataSeriesOfMeasurement(@PathParam("repoId") String repoName, @PathParam("measureId") String measureId);
	
	
	
}
