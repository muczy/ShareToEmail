package org.sharetomail.util;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Util {

	public static CharSequence getEmailAppLabel(String appPackageName,
			PackageManager packageManager) {
		try {
			return packageManager.getApplicationInfo(appPackageName, 0)
					.loadLabel(packageManager);
		} catch (NameNotFoundException e) {
			// TODO: handle better?
			return "";
		}
	}
}
