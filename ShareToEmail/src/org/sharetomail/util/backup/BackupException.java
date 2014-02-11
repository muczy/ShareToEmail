package org.sharetomail.util.backup;

import android.content.res.Resources;

public class BackupException extends Exception {

	private static final long serialVersionUID = -3919241924273426777L;

	private int resourceId;
	private String value;

	public BackupException(int resourceId, String value) {
		super();
		this.resourceId = resourceId;
		this.value = value;
	}

	public String getFormattedMessage(Resources resources) {
		String message;

		if (value == null) {
			message = resources.getString(resourceId);
		} else {
			message = resources.getString(resourceId, value);
		}

		return message;
	}

}
