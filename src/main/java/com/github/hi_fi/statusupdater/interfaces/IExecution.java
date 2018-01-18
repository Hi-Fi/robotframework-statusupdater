package com.github.hi_fi.statusupdater.interfaces;

public interface IExecution {
	
	String createNewExecution();
	
	void updateExecutionStatus(IStatus status);
	
	void updateExecutionStatus(IStatus status, String comment);

}
