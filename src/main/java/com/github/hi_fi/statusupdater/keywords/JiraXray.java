package com.github.hi_fi.statusupdater.keywords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.httpclient.domain.Authentication;
import com.github.hi_fi.statusupdater.jiraxray.model.Evidence;
import com.github.hi_fi.statusupdater.jiraxray.model.Info;
import com.github.hi_fi.statusupdater.jiraxray.model.Test;
import com.github.hi_fi.statusupdater.jiraxray.model.TestExecution;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.Gson;

@RobotKeywords
public class JiraXray {

    private static boolean xrayChecked = false;

    public JiraXray() {
        if (!xrayChecked && !Robot.getRobotVariable("JIRAXRAY_URL", "not set").equals("not set")) {
            Configuration.url = Robot.getRobotVariable("JIRAXRAY_URL")+Robot.getRobotVariable("JIRAXRAY_CONTEXT");
            Configuration.password = Robot.getRobotVariable("JIRAXRAY_PW");
            Configuration.username = Robot.getRobotVariable("JIRAXRAY_USER");
            RestClient rc = new RestClient();
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            rc.createSession("JIRAXRAY", Configuration.url, headers, Authentication.getAuthentication(Arrays.asList(Configuration.username, Configuration.password)), "false", false);
            xrayChecked = true;
        }
    }

    @RobotKeyword("Logs Jira with Xray required varibles.")
    public void logJiraXrayVariables() {
        Logger.log(Robot.getRobotVariable("JIRAXRAY_USER"));
        Logger.log(Robot.getRobotVariable("JIRAXRAY_URL"));
        Logger.log(Robot.getRobotVariable("JIRAXRAY_CONTEXT"));
    }
    
    @RobotKeyword("Import test execution for given testcase with status to Jira with Xray"
            + "\n"
            + "If Execution ID is not available as a Robot variable, new execution is created and ID put to variables. Attachents are added to import if stored with `Add Attachment To List` keyword.")
    public void importXrayTestExecution(String testCaseKey, String status) {
        Info info = Info.builder().summary("Robot test execution").description("Automatic execution from Robot Framework").user(Robot.getRobotVariable("JIRAXRAY_USER")).build();
        List<Evidence> evidences = new ArrayList<Evidence>();
        for (String attachment : Attachment.getAttachmentList()) {
            evidences.add(new Evidence().evidenceWithFilename(attachment));
        }
        Test test = Test.builder().testKey(testCaseKey).status(status).evidences(evidences).build();
        TestExecution te = TestExecution.builder().info(info).tests(Arrays.asList(test)).build();
        if (!Robot.getRobotVariable("EXECUTION_ID", "no_execution_id").equalsIgnoreCase("no_execution_id")) {
            te.setTestExecutionKey(Robot.getRobotVariable("EXECUTION_ID"));
        }
        String jsonPayload = new Gson().toJson(te);
        Logger.logDebug(jsonPayload);
        RestClient rc = new RestClient();
        rc.makePostRequest("JIRAXRAY", "/import/execution", (Object)jsonPayload, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), true);
        Logger.logDebug(rc.getSession("JIRAXRAY").getResponseData());
        new Attachment().clearAttachments();
    }
}
