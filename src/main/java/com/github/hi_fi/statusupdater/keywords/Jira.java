package com.github.hi_fi.statusupdater.keywords;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.statusupdater.jira.ZephyrEntities;
import com.github.hi_fi.statusupdater.jira.ZephyrStatus;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.RequestGenerator;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.RestClient;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.github.hi_fi.statusupdater.utils.TestManagementTool;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RobotKeywords
public class Jira {

	private static boolean zapiChecked = false;

	public Jira() {
		if (!zapiChecked && !Robot.getRobotVariable("JIRA_URL", "not set").equals("not set")) {
			Configuration.url = Robot.getRobotVariable("JIRA_URL");
			Configuration.password = Robot.getRobotVariable("JIRA_PW");
			Configuration.username = Robot.getRobotVariable("JIRA_USER");

			try {
				this.getLicense();
				Configuration.testManagementTool = TestManagementTool.JIRA;
				Configuration.jiraListenersEnabled = new Configuration().anyListenerInUse();
			} catch (Exception e) {
				Logger.logError("ZAPI not available, disabling Jira listeners. Error message was: " + e.getMessage());
				Configuration.jiraListenersEnabled = false;
			}
		}
		zapiChecked = true;
	}

	@RobotKeyword
	public void logJiraVariables() {
		Logger.log(Robot.getRobotVariable("JIRA_USER"));
		Logger.log(Robot.getRobotVariable("JIRA_PW"));
		Logger.log(Robot.getRobotVariable("JIRA_URL"));
		Logger.log(Robot.getRobotVariable("JIRA_CONTEXT"));
	}

	@RobotKeyword
	public void getLicense() throws ParseException, IOException {
		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/license";
		HttpResponse response = RestClient.makeGetCall(URL);
		Logger.log("Licence information: "
				+ new GsonBuilder().setPrettyPrinting().create().toJson(EntityUtils.toString(response.getEntity())));
	}

	@RobotKeyword
	public String createExecution(String cycleId, String projectId, String versionId, String issueId, String assignee) {
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

		HttpResponse response = RestClient.makePostCall(URL, payload,
				new BasicHeader("Content-Type", "application/json"));
		JsonObject responseJsonObject = ResponseParser.parseResponseToJson(response).getAsJsonObject();
		String executionId = responseJsonObject.entrySet().iterator().next().getKey();

		Robot.setRobotTestVariable("EXECUTION_ID", executionId);
		return executionId;
	}

	@RobotKeyword
	public void updateTestStepStatus(String step, String status, String message) {
		JsonObject jsonPayload = new JsonObject();
		jsonPayload.addProperty("status", ZephyrStatus.valueOf(status.toUpperCase()).getStatusCode());
		jsonPayload.addProperty("comment", message);
		StringEntity payload = RequestGenerator.createStringEntityFromString(jsonPayload.toString());

		String stepId = Robot.getRobotVariable("testSteps").split(";")[Integer.parseInt(step) - 1];

		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/stepResult/" + stepId;

		RestClient.makePutCall(URL, payload, new BasicHeader("Content-Type", "application/json"));

		this.uploadAttachment(ZephyrEntities.TESTSTEPRESULT, stepId);
	}

	public void uploadAttachment(ZephyrEntities entity, String entityId) {
		Attachment attachment = new Attachment();
		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/attachment?entityId=" + entityId + "&entityType=" + entity.toString();
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		boolean submissionNeeded = false;
		for (Object createdFile : attachment.getAttachmentList()) {
			submissionNeeded = true;
			File uploadableFile = new File(createdFile.toString());
			builder.addBinaryBody("file", uploadableFile, ContentType.APPLICATION_OCTET_STREAM,
					uploadableFile.getName());
		}

		if (submissionNeeded) {
			HttpEntity payload = builder.build();
			RestClient.makePostUploadCall(URL, payload);
			attachment.clearAttachments();
		}
	}

	@RobotKeyword
	public void updateExecutionStatus(ZephyrStatus status) {
		this.updateExecutionStatus(status, new JsonObject());
	}

	@RobotKeyword
	public String getTestStepIds(String executionId) {
		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/stepResult?executionId=" + executionId;

		ArrayList<String> testSteps = new ArrayList<String>();
		JsonArray responseJsonObject = ResponseParser.parseResponseToJson(RestClient.makeGetCall(URL)).getAsJsonArray();
		for (JsonElement json : responseJsonObject) {
			testSteps.add(json.getAsJsonObject().get("id").getAsString());
		}
		return String.join(";", testSteps);
	}

	public void updateExecutionStatus(ZephyrStatus status, JsonObject jsonPayload) {
		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/zapi/latest/execution";
		URL += "/" + Robot.getRobotVariable("EXECUTION_ID") + "/execute";
		jsonPayload.addProperty("status", status.getStatusCode());

		StringEntity payload = RequestGenerator.createStringEntityFromString(jsonPayload.toString());

		RestClient.makePutCall(URL, payload, new BasicHeader("Content-Type", "application/json"));
	}

	public void updateIdsWithJiraKey(String jiraKey) {
		String URL = Robot.getRobotVariable("JIRA_URL") + Robot.getRobotVariable("JIRA_CONTEXT")
				+ "rest/api/2/search?jql=key%3D" + jiraKey;
		HttpResponse response = RestClient.makeGetCall(URL);
		JsonObject responseJsonObject = ResponseParser.parseResponseToJson(response).getAsJsonObject();
		int resultCount = Integer.parseInt(responseJsonObject.get("total").getAsString());
		if (resultCount == 1) {
			JsonObject issueJsonObject = responseJsonObject.getAsJsonArray("issues").get(0).getAsJsonObject();
			Robot.setRobotTestVariable("issueId", issueJsonObject.get("id").getAsString());
			Robot.setRobotTestVariable("projectId",
					issueJsonObject.getAsJsonObject("fields").getAsJsonObject("project").get("id").getAsString());
		} else {
			throw new RuntimeException(String.format("Expected only one results, but got %s.", resultCount));
		}
	}
}
