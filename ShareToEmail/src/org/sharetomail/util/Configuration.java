package org.sharetomail.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.sharetomail.MainActivity;
import org.sharetomail.R;

import android.content.SharedPreferences;
import android.util.Log;

public class Configuration {

	private static final String TAG = Configuration.class.getName();

	private SharedPreferences sharedPreferences;

	public Configuration(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	private List<EmailAddress> parseEmailAddresses(
			String rawEmailAddressesString) {
		List<EmailAddress> result = new LinkedList<EmailAddress>();

		String[] rawEmailAddresses = rawEmailAddressesString
				.split(Constants.EMAIL_ADDRESSES_SPLIT_REGEXP);

		for (int i = 0; i < rawEmailAddresses.length; i++) {
			if (!rawEmailAddresses[i].isEmpty()) {
				try {
					result.add(new EmailAddress(rawEmailAddresses[i]));
				} catch (ParseException e) {
					if (Util.isInEmailFormat(rawEmailAddresses[i])) {
						EmailAddress convertedEmailAddress = new EmailAddress(
								rawEmailAddresses[i], "", "");
						result.add(convertedEmailAddress);
					} else {
						Log.e(TAG, "Failed to parse line \""
								+ rawEmailAddresses[i] + "\" ==> skipping!");
					}
				}
			}
		}

		return result;
	}

	private void storeEmailAddresses(List<EmailAddress> emailAddresses) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < emailAddresses.size(); i++) {
			builder.append(emailAddresses.get(i).toConfigurationLine());
			if (i != emailAddresses.size() - 1) {
				builder.append(Constants.EMAIL_ADDRESSES_SPLIT_REGEXP);
			}
		}

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				builder.toString());

		editor.commit();
	}

	public List<EmailAddress> getEmailAddresses() {
		return parseEmailAddresses(sharedPreferences.getString(
				Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, ""));
	}

	public void addEmailAddress(EmailAddress item) {
		List<EmailAddress> emailAddresses = getEmailAddresses();
		emailAddresses.add(item);
		storeEmailAddresses(emailAddresses);
	}

	public void setEmailAddress(int location, EmailAddress item) {
		List<EmailAddress> emailAddresses = getEmailAddresses();
		emailAddresses.set(location, item);
		storeEmailAddresses(emailAddresses);
	}

	public void removeEmailAddress(EmailAddress item) {
		List<EmailAddress> emailAddresses = getEmailAddresses();
		emailAddresses.remove(item);
		storeEmailAddresses(emailAddresses);
	}

	public EmailAddress getDefaultEmailAddress() {
		EmailAddress emailAddress = new EmailAddress();

		if (sharedPreferences
				.contains(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY)) {
			String rawEmailAddress = sharedPreferences.getString(
					Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "");
			try {
				emailAddress = new EmailAddress(rawEmailAddress);
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse line \"" + rawEmailAddress
						+ "\" ==> skipping!");
				emailAddress = new EmailAddress();
			}
		}

		return emailAddress;
	}

	public void setDefaultEmailAddress(EmailAddress defaultEmailAddress) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				defaultEmailAddress.toConfigurationLine());

		editor.commit();
	}

	public void clearDefaultEmailAddress() {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.remove(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY);

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

		List<EmailAddress> emailAddresses = getEmailAddresses();

		for (int i = 0; i < emailAddresses.size(); i++) {
			builder.append(emailAddresses.get(i).toConfigurationLine());
			if (i != emailAddresses.size() - 1) {
				builder.append(Constants.EMAIL_ADDRESSES_SPLIT_REGEXP);
			}
		}

		properties.put(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				builder.toString());
		properties.put(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				getDefaultEmailAddress().toConfigurationLine());
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

		String rawEmailAddress = properties.getProperty(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "");
		if (!rawEmailAddress.isEmpty()) {
			try {
				setDefaultEmailAddress(new EmailAddress(rawEmailAddress));
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse line \"" + rawEmailAddress
						+ "\" ==> skipping!");
			}
		}

		setEmailSubjectPrefix(properties.getProperty(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY, ""));

		setAutoUseDefaultEmailAddress(Boolean
				.parseBoolean(properties
						.getProperty(
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_DEFAULT_VALUE)));
	}
}
