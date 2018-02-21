package com.github.hi_fi.statusupdater.jiraxray;

import com.github.hi_fi.statusupdater.interfaces.IStatus;

public enum XrayStatus implements IStatus {
	PASS(1),
    FAIL(2),
    WIP(3),
    BLOCKED(4);


    private final int statusCode;

    XrayStatus(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }

	public String getStatusString() {
		return this.toString();
	}

}
