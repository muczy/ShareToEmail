package org.sharetomail.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

							Time time = new Time();
							time.setToNow();

							printWriter.append(time.format2445() + "\n");
							ex.printStackTrace(printWriter);
							printWriter.append("==========\n");

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
}
