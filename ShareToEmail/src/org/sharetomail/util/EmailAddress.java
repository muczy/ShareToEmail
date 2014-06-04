package org.sharetomail.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class EmailAddress implements Parcelable {

	private String emailAddress = "";
	private String emailAppName = "";
	private String emailAppPackageName = "";

	public EmailAddress() {
		super();
	}

	public EmailAddress(Parcel in) {
		super();
		this.emailAddress = in.readString();
		this.emailAppName = in.readString();
		this.emailAppPackageName = in.readString();
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

	public static final Parcelable.Creator<EmailAddress> CREATOR = new Parcelable.Creator<EmailAddress>() {
		@Override
		public EmailAddress createFromParcel(Parcel in) {
			return new EmailAddress(in);
		}

		@Override
		public EmailAddress[] newArray(int size) {
			return new EmailAddress[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(emailAddress);
		dest.writeString(emailAppName);
		dest.writeString(emailAppPackageName);
	}

	@Override
	public String toString() {
		return emailAddress;
	}
}
