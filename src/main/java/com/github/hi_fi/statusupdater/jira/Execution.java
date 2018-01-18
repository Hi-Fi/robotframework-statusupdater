package com.github.hi_fi.statusupdater.jira;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import com.github.hi_fi.statusupdater.interfaces.IExecution;
import com.github.hi_fi.statusupdater.interfaces.IStatus;
import com.github.hi_fi.statusupdater.utils.RequestGenerator;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.RestClient;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.JsonObject;

public class Execution implements IExecution {

	public String createNewExecution() {
		String cycleId = Robot.getRobotVariable("cycleId", "-1");
		String projectId = Robot.getRobotVariable("projectId", "-1");
		String versionId = Robot.getRobotVariable("versionId", "-1");
		String issueId = Robot.getRobotVariable("issueId", "-1");
		String assignee = Robot.getRobotVariable("assignee", Robot.getRobotVariable("JIRA_USER"));

		JsonObject jsonPayload = new JsonObject();
		jsonPayload.addProperty("cycleId", cycleId);
		jsonPayload.addProperty("issueId", issueId);
		jsonPayload.addProperty("projectId", projectId);
		jsonPayload.addProperty("versionId", versionId);
		jsonPayload.addProperty("assigneeType", "assignee");
		jsonPayload.addProperty("assignee", assignee);

		StringEntity payload = RequestGenerator.createStringEntityFromString(jsonPayload.toString());

		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/execution";

		HttpResponse response = RestClient.makePostCall(URL, payload);
		JsonObject responseJsonObject = ResponseParser.parseResponseToJson(response).getAsJsonObject();
		String executionId = responseJsonObject.entrySet().iterator().next().getKey();

		Robot.setRobotTestVariable("EXECUTION_ID", executionId);
		return executionId;
	}

	public void updateExecutionStatus(IStatus status) {
		this.updateExecutionStatus(status, "");
	}
	
	public void updateExecutionStatus(IStatus status, String comment) {
		JsonObject jsonPayload = new JsonObject();
		jsonPayload.addProperty("comment", comment);
		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/execution";
		URL += "/" + Robot.getRobotVariable("EXECUTION_ID") + "/execute";
		jsonPayload.addProperty("status", status.getStatusCode());

		StringEntity payload = RequestGenerator.createStringEntityFromString(jsonPayload.toString());

		RestClient.makePutCall(URL, payload, new BasicHeader("Content-Type", "application/json"));
	}

}
