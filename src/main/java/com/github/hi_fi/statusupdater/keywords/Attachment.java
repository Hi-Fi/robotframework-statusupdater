package com.github.hi_fi.statusupdater.keywords;

import java.util.ArrayList;
import java.util.List;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class Attachment {

	private static List<String> attachmentList = new ArrayList<String>();
	
	@RobotKeyword
	public void clearAttachments() {
		attachmentList = new ArrayList<String>();
	}
	
	@RobotKeyword
	public void addAttachmentToList(String attachment) {
		attachmentList.add(attachment);
	}
	
	public List<String> getAttachmentList() {
		return attachmentList;
	}
}
