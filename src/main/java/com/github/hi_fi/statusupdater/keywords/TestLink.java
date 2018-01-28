package com.github.hi_fi.statusupdater.keywords;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.github.hi_fi.statusupdater.utils.TestManagementTool;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
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
                Configuration.testManagementTool = TestManagementTool.TESTLINK;
                Configuration.testlinkListenersEnabled = true;
            } catch (TestLinkAPIException e) {
                Logger.logError(String.format("Connection to TestLink failed with URL %s and API key %s. Error thrown was: %s", Configuration.url, Configuration.api_key, e.getMessage()));
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Logger.logError(String.format("Given URL %s doesn't seem to be real URL. Error thrown was: %s", Configuration.url, e.getMessage()));
                e.printStackTrace();
            }
        } else {
            Configuration.testlinkListenersEnabled = false;
        }
    }
    
    @RobotKeyword("Creates a new execution to given testcase with given status. If notes are given, those are also put to testcase. In Listener notes contain last error message from test execution.")
    @ArgumentNames({ "testcaseExternalId", "status", "notes=Empty" })
    public void updateTestLinkExecutionStatusWithExternalId(String testcaseExternalId, String status, String...params) {
        String notes = com.github.hi_fi.javalibbase.Robot.getParamsValue(params, 0, "");
        ExecutionStatus testlinkStatus = ExecutionStatus.getExecutionStatus(status.charAt(0));
        String planName = Robot.getRobotVariable("planName", "-1");
        String projectName = Robot.getRobotVariable("projectName", "-1");
        Integer buildId = Integer.parseInt(Robot.getRobotVariable("buildId", "-1"));
        TestCase tc = testlinkJavaApi.getTestCaseByExternalId(testcaseExternalId, null);
        TestPlan tp = testlinkJavaApi.getTestPlanByName(planName, projectName);
        ReportTCResultResponse tcResultResponse = testlinkJavaApi.setTestCaseExecutionResult(tc.getId(), null, tp.getId(), testlinkStatus, buildId, null, notes, true, null, null, null, null, null);
        Robot.setRobotTestVariable("EXECUTION_ID", tcResultResponse.getExecutionId());
        this.uploadAttachments(testcaseExternalId);
    }
    
    @RobotKeyword("Uploads all attachents put to list with `Add Attachent To List` -keyword to last updated execution. After upload clears the Attachment list.")
    public void uploadAttachments(String testcaseExternalId) {
        for (String attachmentPath : Attachment.getAttachmentList()) {
            File attachment = new File(attachmentPath);
            try {
                testlinkJavaApi.uploadExecutionAttachment(Integer.parseInt(Robot.getRobotVariable("EXECUTION_ID")), attachment.getName(), "", attachment.getName(), URLConnection.getFileNameMap().getContentTypeFor(attachment.getName()), Base64.encodeBase64String(FileUtils.readFileToByteArray(attachment)));
            } catch (NumberFormatException e) {
                Logger.logError("Parsing of integer of execution ID failed. Tried to parse value: "+Robot.getRobotVariable("EXECUTION_ID")+".");
            } catch (TestLinkAPIException e) {
                Logger.logError("Attachent upload failed");
            } catch (IOException e) {
                Logger.logError("Encoding of file failed. Actual error: "+e.getMessage()+".");
            }
        }
        new Attachment().clearAttachments();
    }
}