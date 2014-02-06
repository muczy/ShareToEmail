package org.sharetomail.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.sharetomail.R;

import android.content.SharedPreferences;
import android.content.res.Resources;

public class Configuration {

	private List<String> emailAddresses;
	private String defaultEmailAddress;
	private String emailSubjectPrefix;
	private boolean autoUseDefaultEmailAddress;

	SharedPreferences sharedPreferences;

	public Configuration(SharedPreferences sharedPreferences,
			Resources resources) {
		this.sharedPreferences = sharedPreferences;

		emailAddresses = parseEmailAddresses(sharedPreferences.getString(
				Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, ""));

		defaultEmailAddress = sharedPreferences.getString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "");

		emailSubjectPrefix = sharedPreferences.getString(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				resources.getString(R.string.default_email_subject_prefix));

		autoUseDefaultEmailAddress = sharedPreferences
				.getBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						true);
	}

	public Configuration(SharedPreferences sharedPreferences,
			Properties properties) {
		this.sharedPreferences = sharedPreferences;

		emailAddresses = parseEmailAddresses(properties.getProperty(
				Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, ""));

		defaultEmailAddress = properties.getProperty(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "");

		emailSubjectPrefix = properties.getProperty(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY, "");

		autoUseDefaultEmailAddress = Boolean
				.parseBoolean(properties
						.getProperty(
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_DEFAULT_VALUE));
	}

	private List<String> parseEmailAddresses(String rawEmailAddressesString) {
		List<String> result = new LinkedList<String>();

		String[] rawEmailAddresses = rawEmailAddressesString
				.split(Constants.SPLIT_REGEXP);

		for (int i = 0; i < rawEmailAddresses.length; i++) {
			if (!rawEmailAddresses[i].isEmpty()) {
				emailAddresses.add(rawEmailAddresses[i]);
			}
		}

		return result;
	}

	public List<String> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
		storeSharedPreferences();
	}

	public String getDefaultEmailAddress() {
		return defaultEmailAddress;
	}

	public void setDefaultEmailAddress(String defaultEmailAddress) {
		this.defaultEmailAddress = defaultEmailAddress;
		storeSharedPreferences();
	}

	public String getEmailSubjectPrefix() {
		return emailSubjectPrefix;
	}

	public void setEmailSubjectPrefix(String emailSubjectPrefix) {
		this.emailSubjectPrefix = emailSubjectPrefix;
		storeSharedPreferences();
	}

	public boolean isAutoUseDefaultEmailAddress() {
		return autoUseDefaultEmailAddress;
	}

	public void setAutoUseDefaultEmailAddress(boolean autoUseDefaultEmailAddress) {
		this.autoUseDefaultEmailAddress = autoUseDefaultEmailAddress;
		storeSharedPreferences();
	}

	private void storeSharedPreferences() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < emailAddresses.size(); i++) {
			builder.append(emailAddresses.get(i));
			if (i != emailAddresses.size() - 1) {
				builder.append(Constants.SPLIT_REGEXP);
			}
		}

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				builder.toString())
				.putString(
						Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						defaultEmailAddress)
				.putString(
						Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
						emailSubjectPrefix)
				.putBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						autoUseDefaultEmailAddress).commit();
	}

	public Properties toProperties() {
		Properties properties = new Properties();

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < emailAddresses.size(); i++) {
			builder.append(emailAddresses.get(i));
			if (i != emailAddresses.size() - 1) {
				builder.append(Constants.SPLIT_REGEXP);
			}
		}

		properties.put(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				builder.toString());
		properties.put(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				defaultEmailAddress);
		properties.put(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				emailSubjectPrefix);
		properties
				.put(Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						String.valueOf(autoUseDefaultEmailAddress));

		return properties;
	}
}
