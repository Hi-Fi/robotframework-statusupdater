package com.github.hi_fi.statusupdater.keywords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.httpclient.domain.Authentication;
import com.github.hi_fi.statusupdater.jirazephyr.Execution;
import com.github.hi_fi.statusupdater.jirazephyr.ZephyrEntities;
import com.github.hi_fi.statusupdater.jirazephyr.ZephyrStatus;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.github.hi_fi.statusupdater.utils.TestManagementTool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RobotKeywords
public class JiraZephyr {

    private static boolean zapiChecked = false;

    public JiraZephyr() {
        if (!zapiChecked && !Robot.getRobotVariable("JIRAZEPHYR_URL", "not set").equals("not set")) {
            Configuration.url = Robot.getRobotVariable("JIRAZEPHYR_URL")+Robot.getRobotVariable("JIRAZEPHYR_CONTEXT");
            Configuration.password = Robot.getRobotVariable("JIRAZEPHYR_PW");
            Configuration.username = Robot.getRobotVariable("JIRAZEPHYR_USER");

            try {
                RestClient rc = new RestClient();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                rc.createSession("JIRAZEPHYR", Configuration.url, headers, Authentication.getAuthentication(Arrays.asList(Configuration.username, Configuration.password)), "false", false);
                this.checkZephyrAvailability();
                Configuration.testManagementTool = TestManagementTool.JIRAZEPHYR;
                Configuration.jiraZephyrListenersEnabled = true;
            } catch (Exception e) {
                Logger.logError("ZAPI not available, disabling JiraZephyr listeners. Error message was: " + e.getMessage());
                Configuration.jiraZephyrListenersEnabled = false;
            }
        }
        zapiChecked = true;
    }

    @RobotKeyword("Logs Jira with Zephyr required varibles.")
    public void logJiraZephyrVariables() {
        Logger.log(Robot.getRobotVariable("JIRAZEPHYR_USER"));
        Logger.log(Robot.getRobotVariable("JIRAZEPHYR_URL"));
        Logger.log(Robot.getRobotVariable("JIRAZEPHYR_CONTEXT"));
    }

    /**
     * Used also to check that Zephyr is available.
     * @throws ParseException
     * @throws IOException
     */
    public void checkZephyrAvailability() throws ParseException, IOException {
        RestClient rc = new RestClient();
        rc.makeGetRequest("JIRAZEPHYR", "rest/zapi/latest/util/teststepExecutionStatus", new HashMap<String, String>(), new HashMap<String, String>(), true);
        if (rc.getSession("JIRAZEPHYR").getResponse().getStatusLine().getStatusCode() > 400) {
            throw new RuntimeException(rc.getSession("JIRAZEPHYR").getResponseData().toString());
        }
    }

    @RobotKeyword("Created new Zephyr test execution. With this keyword technical id's of project, version issue and cycle needs to be given."
            + "\n" + "These values can be assigned to Robot variables with keyword `Update Ids With Jira Key`." + "\n"
            + "Execution ID is stored to variable EXECUTION_ID, and required when updating execution status.")
    public String createExecution(String cycleId, String projectId, String versionId, String issueId, String assignee) {
        return new Execution().createNewExecution(cycleId, projectId, versionId, issueId, assignee);
    }

    @RobotKeyword("Updates status of single step in test case.")
    public void updateTestStepStatus(String step, String status, String message) {
        JsonObject jsonPayload = new JsonObject();
        jsonPayload.addProperty("status", ZephyrStatus.valueOf(status.toUpperCase()).getStatusCode());
        jsonPayload.addProperty("comment", message);

        String stepId = Robot.getRobotVariable("testSteps").split(";")[Integer.parseInt(step) - 1];

        RestClient rc = new RestClient();
        rc.makePutRequest("JIRAZEPHYR", "rest/zapi/latest/stepResult/" + stepId, (Object)jsonPayload, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);

        this.uploadAttachment(ZephyrEntities.TESTSTEPRESULT, stepId);
    }

    @RobotKeyword("Returns semicolon separated list of teststep IDs for current testcase under execution.")
    public String getTestStepIds(String executionId) {
        ArrayList<String> testSteps = new ArrayList<String>();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("jql", "executionId" + executionId);
        RestClient rc = new RestClient();
        rc.makeGetRequest("JIRAZEPHYR", "rest/zapi/latest/stepResult", new HashMap<String, String>(), parameters, true);
        JsonArray responseJsonObject = ResponseParser.parseStringToJson(rc.getSession("JIRAZEPHYR").getResponseBody()).getAsJsonArray();
        for (JsonElement json : responseJsonObject) {
            testSteps.add(json.getAsJsonObject().get("id").getAsString());
        }
        return String.join(";", testSteps);
    }

    public void uploadAttachment(ZephyrEntities entity, String entityId) {
        Attachment attachment = new Attachment();
        boolean submissionNeeded = false;
        Map<String, String> files = new HashMap<String, String>();
        for (Object createdFile : attachment.getAttachmentList()) {
            submissionNeeded = true;
            files.put("file", createdFile.toString());
        }

        if (submissionNeeded) {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("entityId", entityId);
            parameters.put("entityType", entity.toString());
            RestClient rc = new RestClient();
            rc.makePostRequest("JIRAZEPHYR", "rest/zapi/latest/attachment", null, new HashMap<String, String>(), new HashMap<String, String>(), files, true);
            attachment.clearAttachments();
        }
    }

    public void updateExecutionStatus(ZephyrStatus status) {
        this.updateExecutionStatus(status, new JsonObject());
    }

    public void updateExecutionStatus(ZephyrStatus status, JsonObject jsonPayload) {
        String URI = "rest/zapi/latest/execution/" + Robot.getRobotVariable("EXECUTION_ID") + "/execute";
        jsonPayload.addProperty("status", status.getStatusCode());

        RestClient rc = new RestClient();
        rc.makePutRequest("JIRAZEPHYR", URI, (Object)jsonPayload, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);
    }

    @RobotKeyword("Updates Issue's technical ID and project's technical ID using Jira-key")
    public void updateIdsWithJiraKey(String jiraKey) {
        String URI = "rest/api/2/search";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("jql", "key%3D" + jiraKey);
        RestClient rc = new RestClient();
        rc.makeGetRequest("JIRAZEPHYR", URI, new HashMap<String, String>(), parameters, true);
        
        JsonObject responseJsonObject = ResponseParser.parseStringToJson(rc.getSession("JIRAZEPHYR").getResponseBody()).getAsJsonObject();
        int resultCount = Integer.parseInt(responseJsonObject.get("total").getAsString());
        if (resultCount == 1) {
            JsonObject issueJsonObject = responseJsonObject.getAsJsonArray("issues").get(0).getAsJsonObject();
            Robot.setRobotTestVariable("issueId", issueJsonObject.get("id").getAsString());
            Robot.setRobotTestVariable("projectId",
                    issueJsonObject.getAsJsonObject("fields").getAsJsonObject("project").get("id").getAsString());
        } else {
            Logger.logError(String.format("Expected only one results, but got %s.", resultCount));
            throw new RuntimeException(String.format("Expected only one results, but got %s.", resultCount));
        }
    }
}
