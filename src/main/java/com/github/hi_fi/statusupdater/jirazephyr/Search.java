package com.github.hi_fi.statusupdater.jirazephyr;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.statusupdater.interfaces.ISearch;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.JsonObject;

public class Search implements ISearch {

	public void loadIdsForExecution(String jiraKey) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("jql", "key%3D" + jiraKey);
        RestClient rc = new RestClient();
        rc.makeGetRequest("JIRAZEPHYR", "rest/api/2/search", new HashMap<String, String>(), parameters, true);
		JsonObject responseJsonObject = ResponseParser.parseStringToJson(rc.getSession("JIRAZEPHYR").getResponseBody()).getAsJsonObject();
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
