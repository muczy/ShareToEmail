package org.sharetomail.util;

import org.json.JSONException;
import org.json.JSONObject;

public class EmailAddress {

	private String emailAddress = "";
	private String emailAppName = "";
	private String emailAppPackageName = "";

	public EmailAddress() {
		super();
	}

	public EmailAddress(String emailAddress, String emailAppName,
			String emailAppPackageName) {
		super();
		this.emailAddress = emailAddress;
		this.emailAppName = emailAppName;
		this.emailAppPackageName = emailAppPackageName;
	}

	public EmailAddress(String configurationLine) throws ParseException {
		super();

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(configurationLine);
		} catch (JSONException e) {
			throw new ParseException("JSON parsing failed.", e);
		}

		emailAddress = jsonObject.optString(
				Constants.EMAIL_ADDRESS_JSON_EMAIL_ADDRESS, "");
		emailAppName = jsonObject.optString(
				Constants.EMAIL_ADDRESS_JSON_EMAIL_APP_NAME, "");
		emailAppPackageName = jsonObject.optString(
				Constants.EMAIL_ADDRESS_JSON_EMAIL_APP_PACKAGE_NAME, "");
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAppName() {
		return emailAppName;
	}

	public String getEmailAppPackageName() {
		return emailAppPackageName;
	}

	public String toConfigurationLine() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Constants.EMAIL_ADDRESS_JSON_EMAIL_ADDRESS,
					emailAddress);
			jsonObject.put(Constants.EMAIL_ADDRESS_JSON_EMAIL_APP_NAME,
					emailAppName);
			jsonObject.put(Constants.EMAIL_ADDRESS_JSON_EMAIL_APP_PACKAGE_NAME,
					emailAppPackageName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject.toString();
	}
}
