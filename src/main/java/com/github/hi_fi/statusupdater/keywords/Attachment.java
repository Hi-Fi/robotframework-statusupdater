package com.github.hi_fi.statusupdater.keywords;

import java.util.ArrayList;
import java.util.List;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class Attachment {

	private static List<String> attachmentList = new ArrayList<String>();
	
	@RobotKeyword("Clears attachment list")
	public void clearAttachments() {
		attachmentList = new ArrayList<String>();
	}
	
	@RobotKeyword("Add attachment (full path to file) to list of attachments to be submitted with testcase. Currently implemented for Jira when submitting test specific results and for Testlink's executions.")
	@ArgumentNames({"attachment"})
	public void addAttachmentToList(String attachment) {
		attachmentList.add(attachment);
	}
	
	public static List<String> getAttachmentList() {
		return attachmentList;
	}
}
