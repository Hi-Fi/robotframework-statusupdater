package com.github.hi_fi.statusupdater.jirazephyr;

import org.apache.http.HttpResponse;

import com.github.hi_fi.statusupdater.interfaces.ISearch;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.RestClient;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.JsonObject;

public class Search implements ISearch {

	public void loadIdsForExecution(String jiraKey) {
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
