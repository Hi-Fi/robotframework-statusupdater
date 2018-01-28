package com.github.hi_fi.statusupdater.keywords;

import java.util.Map;

import com.github.hi_fi.statusupdater.jira.ZephyrStatus;
import com.github.hi_fi.statusupdater.qc.Execution;
import com.github.hi_fi.statusupdater.qc.QcStatus;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.google.gson.JsonObject;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;

public class Listener {
    
	public void startSuite(String name, Map attrs) {
    }
	
	public void startTest(String name, Map attrs) {
		if (Configuration.jiraListenersEnabled) {
			Jira jira = new Jira();
			String jiraKey = name.split(" ")[0];
			jira.updateIdsWithJiraKey(jiraKey);
			String cycleId = Robot.getRobotVariable("cycleId", "-1");
			String projectId = Robot.getRobotVariable("projectId", "-1");
			String versionId = Robot.getRobotVariable("versionId", "-1");
			String issueId = Robot.getRobotVariable("issueId", "-1");
			String assignee = Robot.getRobotVariable("assignee", Robot.getRobotVariable("JIRA_USER"));
			jira.createExecution(cycleId, projectId, versionId, issueId, assignee);
			Robot.setRobotTestVariable("testSteps", jira.getTestStepIds(Robot.getRobotVariable("EXECUTION_ID")));
			jira.updateExecutionStatus(ZephyrStatus.WIP);
		} else if (Configuration.qcListenersEnabled) {
			Execution execution = new Execution(Robot.getRobotVariable("QC_DOMAIN"), Robot.getRobotVariable("QC_PROJECT"));
			execution.createNewExecution();
		}
    }
	
	public void endTest(String name, Map attrs) {
		String status = attrs.get("status").toString();
		if (Configuration.jiraListenersEnabled) {
			Jira jira = new Jira();
			JsonObject jsonPayload = new JsonObject();
			if (status == "PASS") {
				String comment = String.format("Automated test run for test %s", 
						attrs.get("longname").toString());
				jsonPayload.addProperty("comment", comment);
				jira.updateExecutionStatus(ZephyrStatus.PASS, jsonPayload);
			} else {
				String comment = String.format("Automated test run for test %s failed with message: %s", 
						attrs.get("longname").toString(),
						attrs.get("message").toString());
				jsonPayload.addProperty("comment", comment);
				jira.updateExecutionStatus(ZephyrStatus.FAIL, jsonPayload);
			}
		} else if (Configuration.qcListenersEnabled) {
			Execution execution = new Execution(Robot.getRobotVariable("QC_DOMAIN"), Robot.getRobotVariable("QC_PROJECT"));
			QcStatus qcStatus = QcStatus.PASSED;
			if (status.equals("FAIL")) {
				boolean blocked = attrs.get("tags").toString().toUpperCase().contains("BLOCKED");
				qcStatus = blocked ? QcStatus.BLOCKED : QcStatus.FAILED;
			}
			execution.updateExecutionStatus(qcStatus);
		} else if (Configuration.testlinkListenersEnabled) {
		    if (status.equals("FAIL")) {
                boolean blocked = attrs.get("tags").toString().toUpperCase().contains("BLOCKED");
                status = blocked ? "Blocked": "Failed";
            }
		    String testcaseExternalId = name.split(" ")[0];
	        String notes = attrs.get("message").toString();
	        new TestLink().updateTestLinkExecutionStatusWithExternalId(testcaseExternalId, status, notes);
		}
    }
	
	public void message(Map message) {
		System.out.println("WARN: Listener message");
    }
}
