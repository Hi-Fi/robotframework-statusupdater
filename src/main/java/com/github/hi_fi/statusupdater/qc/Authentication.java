package com.github.hi_fi.statusupdater.qc;

import java.util.HashMap;
import java.util.Map;

import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.statusupdater.qc.infrastructure.Base64Encoder;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.TestManagementTool;

@RobotKeywords
public class Authentication {

	private String loginPathQC12 = "/qcbin/api/authentication/sign-in";
	private String loginPathQC11 = "/qcbin/authentication-point/authenticate";
	
	private static Boolean authenticated = false;
	
	public Authentication() {
		if (!authenticated && new Configuration().getTestManagementTool().equals(TestManagementTool.QC)) {
			this.loginToQC();
			authenticated = isAuthenticated();
		}
	}

	public void loginToQC() {
		this.loginToQC(Configuration.username, Configuration.password);
	}

	public void loginToQC(String username, String password) {
		login(username, password);
	}

	public void logoutFromQC() {
		this.logout();
	}

	/**
	 * @param username QC username
	 * @param password QC password
	 * @return Boolean true if authenticated at the end of this method.
	 *
	 * convenience method used by other examples to do their login
	 */
	public boolean login(String username, String password) {

		if (!this.isAuthenticated()) {
			this.login(this.loginPathQC12, username, password);
		}
		return true;
	}

	/**
	 * @param loginUrl
	 *            to authenticate at
	 * @param username QC username
	 * @param password QC password
	 */
	public void login(String loginUri, String username, String password) {
		// create a string that looks like:
		// "Basic ((username:password)<as bytes>)<64encoded>"
		byte[] credBytes = (username + ":" + password).getBytes();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + Base64Encoder.encode(credBytes));
        RestClient rc = new RestClient();
        rc.makePostRequest("JIRAZEPHYR", loginUri, null, new HashMap<String, String>(), headers, new HashMap<String, String>(), true);
	}

	public void logout() {
	    RestClient rc = new RestClient();
        rc.makeGetRequest("QC", "/qcbin/authentication-point/logout", new HashMap<String, String>(), new HashMap<String, String>(), true);
		authenticated = false;
	}

	/**
	 * @return Boolean true if authenticated.<br>
	 */
	public boolean isAuthenticated() {

		String isAuthenticateUri = "/qcbin/rest/is-authenticated";

		try {
		    RestClient rc = new RestClient();
	        rc.makeGetRequest("QC", isAuthenticateUri, new HashMap<String, String>(), new HashMap<String, String>(), true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}