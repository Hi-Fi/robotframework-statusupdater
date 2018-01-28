package com.github.hi_fi.statusupdater.utils;

public class Configuration {
	
	private static boolean configurationObtained = false;
	public static boolean jiraListenersEnabled = false;
	public static boolean qcListenersEnabled = false;
	public static boolean testlinkListenersEnabled = false;
	public static boolean startSuiteListenerEnabled = false;
	public static boolean stopSuiteListenerEnabled = false;
	public static boolean startTestListenerEnabled = false;
	public static boolean stopTestListenerEnabled = false;
	public static String username = "";
	public static String password = "";
	public static String url = "";
	public static String api_key = "";
	public static TestManagementTool testManagementTool = null;
	
	
	
	public Configuration() {
		if (!configurationObtained) {
			startSuiteListenerEnabled = Boolean.valueOf(Robot.getRobotVariable("startSuiteListenerEnabled"));
			stopSuiteListenerEnabled = Boolean.valueOf(Robot.getRobotVariable("stopSuiteListenerEnabled"));
			startTestListenerEnabled = Boolean.valueOf(Robot.getRobotVariable("startTestListenerEnabled"));
			stopTestListenerEnabled = Boolean.valueOf(Robot.getRobotVariable("stopTestListenerEnabled"));
			configurationObtained = true;
		}
	}
	
	public TestManagementTool getTestManagementTool() {
		return testManagementTool;
	}
	
	public boolean anyListenerInUse() {
		return startSuiteListenerEnabled || stopSuiteListenerEnabled || startTestListenerEnabled || stopTestListenerEnabled;
	}
	
	public boolean listenSuiteStart() {
		return this.testManagementAvailable() && startSuiteListenerEnabled;
	}
	
	public boolean listenSuiteStop() {
		return this.testManagementAvailable() && stopSuiteListenerEnabled;
	}
	
	public boolean listenTestStart() {
		return this.testManagementAvailable() && startTestListenerEnabled;
	}
	
	public boolean listenTestStop() {
		return this.testManagementAvailable() && stopTestListenerEnabled;
	} 
	
	private boolean testManagementAvailable() {
		return jiraListenersEnabled || qcListenersEnabled || testlinkListenersEnabled;
	}
}
