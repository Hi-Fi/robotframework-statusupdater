package com.github.hi_fi.statusupdater.qc;

import org.apache.http.message.BasicHeader;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.hi_fi.statusupdater.qc.infrastructure.Base64Encoder;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.RestClient;
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
	 * @param username
	 * @param password
	 * @return true if authenticated at the end of this method.
	 * @throws Exception
	 *
	 *             convenience method used by other examples to do their login
	 */
	public boolean login(String username, String password) {

		if (!this.isAuthenticated()) {
			this.login(Configuration.url + this.loginPathQC12, username, password);
		}
		return true;
	}

	/**
	 * @param loginUrl
	 *            to authenticate at
	 * @param username
	 * @param password
	 */
	public void login(String loginUrl, String username, String password) {
		// create a string that looks like:
		// "Basic ((username:password)<as bytes>)<64encoded>"
		byte[] credBytes = (username + ":" + password).getBytes();
		RestClient.makePostCall(loginUrl, null,
				new BasicHeader("Authorization", "Basic " + Base64Encoder.encode(credBytes)));
	}

	/**
	 * @return true if logout successful
	 */
	public void logout() {
		RestClient.makeGetCall(Configuration.url + "/qcbin/authentication-point/logout");
		authenticated = false;
	}

	/**
	 * @return null if authenticated.<br>
	 *         a url to authenticate against if not authenticated.
	 * @throws Exception
	 */
	public boolean isAuthenticated() {

		String isAuthenticateUrl = Configuration.url + "/qcbin/rest/is-authenticated";

		try {
			RestClient.makeGetCall(isAuthenticateUrl);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}