package org.sharetomail.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.sharetomail.MainActivity;
import org.sharetomail.R;

import android.content.SharedPreferences;

public class Configuration {

	private SharedPreferences sharedPreferences;

	public Configuration(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	private List<String> parseEmailAddresses(String rawEmailAddressesString) {
		List<String> result = new LinkedList<String>();

		String[] rawEmailAddresses = rawEmailAddressesString
				.split(Constants.SPLIT_REGEXP);

		for (int i = 0; i < rawEmailAddresses.length; i++) {
			if (!rawEmailAddresses[i].isEmpty()) {
				result.add(rawEmailAddresses[i]);
			}
		}

		return result;
	}

	private void storeEmailAddresses(List<String> emailAddresses) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < emailAddresses.size(); i++) {
			builder.append(emailAddresses.get(i));
			if (i != emailAddresses.size() - 1) {
				builder.append(Constants.SPLIT_REGEXP);
			}
		}

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				builder.toString());

		editor.commit();
	}

	public List<String> getEmailAddresses() {
		return parseEmailAddresses(getEmailAddressesAsString());
	}

	public String getEmailAddressesAsString() {
		return sharedPreferences.getString(
				Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, "");
	}

	public void setEmailAddressesFromString(String string) {
		storeEmailAddresses(parseEmailAddresses(string));
	}

	public void addEmailAddress(String item) {
		List<String> emailAddresses = getEmailAddresses();
		emailAddresses.add(item);
		storeEmailAddresses(emailAddresses);
	}

	public void setEmailAddress(int location, String item) {
		List<String> emailAddresses = getEmailAddresses();
		emailAddresses.set(location, item);
		storeEmailAddresses(emailAddresses);
	}

	public void removeEmailAddress(String item) {
		List<String> emailAddresses = getEmailAddresses();
		emailAddresses.remove(item);
		storeEmailAddresses(emailAddresses);
	}

	public String getDefaultEmailAddress() {
		return sharedPreferences.getString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "");
	}

	public void setDefaultEmailAddress(String defaultEmailAddress) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				defaultEmailAddress);

		editor.commit();
	}

	public String getEmailSubjectPrefix() {
		String defaultValue = MainActivity.getResourcesObject().getString(
				R.string.default_email_subject_prefix);
		return sharedPreferences.getString(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				defaultValue);
	}

	public void setEmailSubjectPrefix(String emailSubjectPrefix) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				emailSubjectPrefix);

		editor.commit();
	}

	public boolean isAutoUseDefaultEmailAddress() {
		return sharedPreferences
				.getBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						true);
	}

	public void setAutoUseDefaultEmailAddress(boolean autoUseDefaultEmailAddress) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putBoolean(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				autoUseDefaultEmailAddress);

		editor.commit();
	}

	public Properties toProperties() {
		Properties properties = new Properties();

		StringBuilder builder = new StringBuilder();

		List<String> emailAddresses = getEmailAddresses();

		for (int i = 0; i < emailAddresses.size(); i++) {
			builder.append(emailAddresses.get(i));
			if (i != emailAddresses.size() - 1) {
				builder.append(Constants.SPLIT_REGEXP);
			}
		}

		properties.put(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				builder.toString());
		properties.put(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				getDefaultEmailAddress());
		properties.put(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				getEmailSubjectPrefix());
		properties
				.put(Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						String.valueOf(isAutoUseDefaultEmailAddress()));

		return properties;
	}

	public void loadProperties(Properties properties) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				properties.getProperty(
						Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, ""));

		editor.commit();

		setDefaultEmailAddress(properties.getProperty(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, ""));

		setEmailSubjectPrefix(properties.getProperty(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY, ""));

		setAutoUseDefaultEmailAddress(Boolean
				.parseBoolean(properties
						.getProperty(
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_DEFAULT_VALUE)));
	}
}
