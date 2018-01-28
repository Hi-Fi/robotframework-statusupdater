package com.github.hi_fi.statusupdater.keywords;

import java.net.MalformedURLException;
import java.net.URL;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.github.hi_fi.statusupdater.utils.TestManagementTool;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

@RobotKeywords
public class TestLink {

    public static TestLinkAPI testlinkJavaApi;

    public TestLink() {
        if (!Robot.getRobotVariable("TESTLINK_URL", "not set").equals("not set")) {
            Configuration.url = Robot.getRobotVariable("TESTLINK_URL");
            Configuration.api_key = Robot.getRobotVariable("TESTLINK_API_KEY");
            try {
                testlinkJavaApi = new TestLinkAPI(new URL(Configuration.url), Configuration.api_key);
            } catch (TestLinkAPIException e) {
                Logger.logError(String.format("Connection to TestLink failed with URL %s and API key %s. Error thrown was: %s", Configuration.url, Configuration.api_key, e.getMessage()));
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Logger.logError(String.format("Given URL %s doesn't seem to be real URL. Error thrown was: %s", Configuration.url, e.getMessage()));
                e.printStackTrace();
            }
            Configuration.testManagementTool = TestManagementTool.TESTLINK;
            Configuration.testlinkListenersEnabled = true;
        } else {
            Configuration.testlinkListenersEnabled = false;
        }
    }
    
    @RobotKeyword
    @ArgumentNames({ "testcaseExternalId", "status", "notes=Empty" })
    public void updateTestLinkExecutionStatusWithExternalId(String testcaseExternalId, String status, String...params) {
        String notes = com.github.hi_fi.javalibbase.Robot.getParamsValue(params, 0, "");
        ExecutionStatus testlinkStatus = ExecutionStatus.getExecutionStatus(status.charAt(0));
        String planName = Robot.getRobotVariable("planName", "-1");
        String projectName = Robot.getRobotVariable("projectName", "-1");
        Integer buildId = Integer.parseInt(Robot.getRobotVariable("buildId", "-1"));
        TestCase tc = testlinkJavaApi.getTestCaseByExternalId(testcaseExternalId, null);
        TestPlan tp = testlinkJavaApi.getTestPlanByName(planName, projectName);
        testlinkJavaApi.setTestCaseExecutionResult(tc.getId(), null, tp.getId(), testlinkStatus, buildId, null, notes, true, null, null, null, null, null);
    }

}
