package com.github.hi_fi.statusupdater.jirazephyr;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.statusupdater.interfaces.IExecution;
import com.github.hi_fi.statusupdater.interfaces.IStatus;
import com.github.hi_fi.statusupdater.utils.RequestGenerator;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.JsonObject;

public class Execution implements IExecution {

    public String createNewExecution() {
        String cycleId = Robot.getRobotVariable("cycleId", "-1");
        String projectId = Robot.getRobotVariable("projectId", "-1");
        String versionId = Robot.getRobotVariable("versionId", "-1");
        String issueId = Robot.getRobotVariable("issueId", "-1");
        String assignee = Robot.getRobotVariable("assignee", Robot.getRobotVariable("JIRAZEPHYR_USER"));
        
        return this.createNewExecution(cycleId, projectId, versionId, issueId, assignee);
    }

    public String createNewExecution(String cycleId, String projectId, String versionId, String issueId, String assignee) {
        JsonObject jsonPayload = new JsonObject();
        jsonPayload.addProperty("cycleId", cycleId);
        jsonPayload.addProperty("issueId", issueId);
        jsonPayload.addProperty("projectId", projectId);
        jsonPayload.addProperty("versionId", versionId);
        jsonPayload.addProperty("assigneeType", "assignee");
        jsonPayload.addProperty("assignee", assignee);

        RestClient rc = new RestClient();
        rc.makePostRequest("JIRAZEPHYR", "rest/zapi/latest/execution", (Object)jsonPayload, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);
        JsonObject responseJsonObject = ResponseParser.parseStringToJson(rc.getSession("JIRAZEPHYR").getResponseBody()).getAsJsonObject();
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
        jsonPayload.addProperty("status", status.getStatusCode());

        RestClient rc = new RestClient();
        rc.makePutRequest("JIRAZEPHYR", "rest/zapi/latest/execution/" + Robot.getRobotVariable("EXECUTION_ID") + "/execute", (Object)jsonPayload, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);
    }

}
