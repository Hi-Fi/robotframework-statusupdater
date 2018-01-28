package com.github.hi_fi.statusupdater.qc;

import com.github.hi_fi.statusupdater.interfaces.IStatus;

public enum QcStatus implements IStatus{
	NOT_COMPLETED("Not Completed"),
	BLOCKED("Blocked"),
	PASSED("Passed"),
	FAILED("Failed"),
	NA("N/A"),
	NO_RUN("No Run");
	
	private final String statusString;
	
	QcStatus(String status) {
		this.statusString = status;
	}
	
	@Override
	public String toString() {
		return this.statusString;
	}

	public int getStatusCode() {
		//Not valid in QC
		return 0;
	}

	public String getStatusString() {
		return this.statusString;
	}

}
