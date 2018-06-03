package com.github.hi_fi.statusupdater.qc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.statusupdater.interfaces.IExecution;
import com.github.hi_fi.statusupdater.interfaces.IStatus;
import com.github.hi_fi.statusupdater.qc.infrastructure.TestInstance;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.RequestGenerator;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Execution implements IExecution {

	private String domain;
	private String project;

	public Execution(String domain, String project) {
		new Authentication();
		this.domain = domain;
		this.project = project;
	}

	public String createNewExecution() {
		String URI = "/qcbin/rest/domains/" + domain + "/projects/" + project + "/runs/";

        RestClient rc = new RestClient();
        rc.makePostRequest("QC", URI, this.createTestInstanceMessage(QcStatus.NOT_COMPLETED), new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);
        HttpResponse response = rc.getSession("QC").getResponse();
		String responseString = ResponseParser.parseResponseToString(response);
		String[] locationHeader = response.getFirstHeader("location").getValue().split("/");
		String executionId = locationHeader[locationHeader.length-1];
		Logger.logDebug(responseString);
		Logger.logDebug(executionId);
		
		Robot.setRobotTestVariable("EXECUTION_ID", executionId);
		return executionId;
	}

	public void updateExecutionStatus(IStatus status) {
		this.updateExecutionStatus(status, "");

	}

	public void updateExecutionStatus(IStatus status, String comment) {
		String URI = "/qcbin/rest/domains/" + domain + "/projects/" + project + "/runs/" + Robot.getRobotVariable("EXECUTION_ID");

		RestClient rc = new RestClient();
        rc.makePostRequest("QC", URI, this.createTestInstanceMessage(status), new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);
        HttpResponse response = rc.getSession("QC").getResponse();
        
		Logger.logDebug(ResponseParser.parseResponseToString(response));
	}

	private JsonObject createQCFieldObject(String name, String value) {
		JsonArray jsonArray = new JsonArray();
		JsonObject valueObject = new JsonObject();
		valueObject.addProperty("value", value);
		jsonArray.add(valueObject);
		JsonObject field = new JsonObject();
		field.addProperty("Name", name);
		field.add("values", jsonArray);
		return field;
	}
	
	private JsonObject createTestInstanceMessage(IStatus status) {
		String runname = "auto_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date());;
		JsonArray jsonArray = new JsonArray();
		Search search = new Search();
		TestInstance ti = search.getTestInstancesList().getInstance(Robot.getRobotVariable("TEST_NAME").split(" ")[0]);
		jsonArray.add(this.createQCFieldObject("name", runname));
		jsonArray.add(this.createQCFieldObject("subtype-id", "hp.qc.run.MANUAL"));
		jsonArray.add(this.createQCFieldObject("status", status.toString()));
		jsonArray.add(this.createQCFieldObject("test-id", ti.getTestId().toString()));
		jsonArray.add(this.createQCFieldObject("testcycl-id", ti.getId().toString()));
		jsonArray.add(this.createQCFieldObject("owner", Robot.getRobotVariable("QC_USER")));
		JsonObject jsonPayload = new JsonObject();
		jsonPayload.add("Fields", jsonArray);

		Logger.logDebug(jsonPayload);
		
		return jsonPayload;
	}

}
