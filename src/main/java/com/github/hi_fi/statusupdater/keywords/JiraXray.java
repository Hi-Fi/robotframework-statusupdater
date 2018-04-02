package com.github.hi_fi.statusupdater.keywords;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.statusupdater.jiraxray.model.Info;
import com.github.hi_fi.statusupdater.jiraxray.model.Test;
import com.github.hi_fi.statusupdater.jiraxray.model.TestExecution;
import com.github.hi_fi.statusupdater.qc.infrastructure.Response;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.RequestGenerator;
import com.github.hi_fi.statusupdater.utils.RestClient;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.Gson;

@RobotKeywords
public class JiraXray {

    private static boolean xrayChecked = false;

    public JiraXray() {
        if (!xrayChecked && !Robot.getRobotVariable("JIRAXRAY_URL", "not set").equals("not set")) {
            Configuration.url = Robot.getRobotVariable("JIRAXRAY_URL");
            Configuration.password = Robot.getRobotVariable("JIRAXRAY_PW");
            Configuration.username = Robot.getRobotVariable("JIRAXRAY_USER");
            xrayChecked = true;
        }
    }

    @RobotKeyword("Logs Jira with Xray required varibles. Note that also PW is written to log.")
    public void logJiraXrayVariables() {
        Logger.log(Robot.getRobotVariable("JIRAXRAY_USER"));
        Logger.log(Robot.getRobotVariable("JIRAXRAY_PW"));
        Logger.log(Robot.getRobotVariable("JIRAXRAY_URL"));
        Logger.log(Robot.getRobotVariable("JIRAXRAY_CONTEXT"));
    }
    
    @RobotKeyword("Updates given tests status to Jira with Xray"
            + "\n"
            + "If Execution ID is not available as a Robot variable, new execution is created and ID put to variables")
    public void updateXrayTestStatus(String testCaseKey, String status) throws UnsupportedOperationException, IOException {
        Info info = Info.builder().summary("Robot test execution").description("Automatic execution from Robot Framework").user(Robot.getRobotVariable("JIRAXRAY_USER")).build();
        Test test = Test.builder().testKey(testCaseKey).status(status).build();
        TestExecution te = TestExecution.builder().info(info).tests(Arrays.asList(test)).build();
        if (!Robot.getRobotVariable("EXECUTION_ID", "no_execution_id").equalsIgnoreCase("no_execution_id")) {
            te.setTestExecutionKey(Robot.getRobotVariable("EXECUTION_ID"));
        }
        String URL = Robot.getRobotVariable("JIRAXRAY_URL") + Robot.getRobotVariable("JIRAXRAY_CONTEXT")+"/import/execution".toString();
        String jsonPayload = new Gson().toJson(te);
        Logger.logDebug(URL);
        Logger.logDebug(jsonPayload);
        HttpResponse response = RestClient.makePostCall(URL, RequestGenerator.createStringEntityFromString(jsonPayload), new BasicHeader("Content-Type", "application/json"));
        Logger.logDebug(EntityUtils.toString(response.getEntity(), "UTF-8"));
    }
}
