package com.github.hi_fi.statusupdater.keywords;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.statusupdater.qc.Authentication;
import com.github.hi_fi.statusupdater.qc.Execution;
import com.github.hi_fi.statusupdater.qc.QcStatus;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Robot;
import com.github.hi_fi.statusupdater.utils.TestManagementTool;

@RobotKeywords
public class QC {

	public QC() {
		if (!Robot.getRobotVariable("QC_URL", "not set").equals("not set")) {
			Configuration.url = Robot.getRobotVariable("QC_URL");
			Configuration.password = Robot.getRobotVariable("QC_PW");
			Configuration.username = Robot.getRobotVariable("QC_USER");
			Configuration.testManagementTool = TestManagementTool.QC;
			Configuration.qcListenersEnabled = new Configuration().anyListenerInUse();
			new Authentication();
		} else {
			Configuration.qcListenersEnabled = false;
		}
	}

	@RobotKeyword
	public void loginToQC(String username, String password) {
		Authentication qc = new Authentication();
		qc.loginToQC(username, password);
		if (!qc.isAuthenticated()) {
			throw new RuntimeException("Authentication failed");
		}
	}

	@RobotKeyword
	public void createQcExecution() {
		Execution execution = new Execution(Robot.getRobotVariable("QC_DOMAIN"), Robot.getRobotVariable("QC_PROJECT"));
		execution.createNewExecution();
	}

	@RobotKeyword
	public void updateQcExecutionStatus(String statusString) {
		Execution execution = new Execution(Robot.getRobotVariable("QC_DOMAIN"), Robot.getRobotVariable("QC_PROJECT"));
		QcStatus status = QcStatus.valueOf(statusString.toUpperCase());
		execution.updateExecutionStatus(status);
	}

}
