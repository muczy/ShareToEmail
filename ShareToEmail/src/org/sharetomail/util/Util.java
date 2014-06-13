package org.sharetomail.util;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
}
