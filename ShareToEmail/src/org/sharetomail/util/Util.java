package org.sharetomail.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Patterns;

public class Util {

	public static CharSequence getEmailAppLabel(String appPackageName,
			PackageManager packageManager) {
		try {
			return packageManager.getApplicationInfo(appPackageName, 0)
					.loadLabel(packageManager);
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	// NOTE: The default Android Patterns.EMAIL_ADDRESS does not
	// seems to be handling user@host (host without TLD) so we try
	// to workaround this here by appending a ".com" string.
	public static boolean isInEmailFormat(String text) {
		return Patterns.EMAIL_ADDRESS.matcher(text).matches()
				|| Patterns.EMAIL_ADDRESS.matcher(
						text + Constants.EMAIL_HOSTNAME_TLD).matches();
	}

	private static String getFormattedTime() {
		Time time = new Time();
		time.setToNow();

		return time.format2445();
	}

	public static void uncaughtExceptionHandling() {
		Thread.currentThread().setUncaughtExceptionHandler(
				new Thread.UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread thread, Throwable ex) {

						PrintWriter printWriter;
						try {
							String filename = Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
									+ File.separator
									+ Constants.ERROR_LOG_FILE_NAME;
							printWriter = new PrintWriter(new FileWriter(
									filename, true));

							printWriter.append(getFormattedTime() + "\n");
							ex.printStackTrace(printWriter);
							printWriter.append(Constants.LOG_MESSAGE_SEPARATOR);

							printWriter.flush();
							printWriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

						Thread.getDefaultUncaughtExceptionHandler()
								.uncaughtException(thread, ex);
					}
				});
	}

	public static void writeDebugLog(SharedPreferences sharedPreferences,
			String text) {
		if (sharedPreferences
				.contains(Constants.DEBUG_LOG_ENABLED_SHARED_PREFERENCES_KEY)
				&& sharedPreferences.getBoolean(
						Constants.DEBUG_LOG_ENABLED_SHARED_PREFERENCES_KEY,
						Constants.DEBUG_LOG_ENABLED_DEFAULT_VALUE)) {
			writeDebugLog(text);
		}
	}

	private static void writeDebugLog(String text) {
		PrintWriter printWriter;
		try {
			String filename = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
					+ File.separator + Constants.LOG_FILE_NAME;
			printWriter = new PrintWriter(new FileWriter(filename, true));

			printWriter.append(getFormattedTime() + "\n");
			printWriter.append(Constants.LOG_MESSAGE_SEPARATOR);

			printWriter.flush();
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String dumpIntent(Intent i) {
		StringBuilder builder = new StringBuilder();

		Bundle bundle = i.getExtras();
		if (bundle != null) {
			Set<String> keys = bundle.keySet();
			Iterator<String> it = keys.iterator();
			builder.append("Dumping Intent start");
			while (it.hasNext()) {
				String key = it.next();
				builder.append("[" + key + "=" + bundle.get(key) + "]");
			}
			builder.append("Dumping Intent end");
		}

		return builder.toString();
	}
}
