package org.sharetomail.util.backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.sharetomail.R;
import org.sharetomail.util.Configuration;
import org.sharetomail.util.Constants;

import android.os.Environment;

public class ConfigurationBackupAgent {
	public static void onBackup(Configuration config) throws BackupException {
		if (!isExternalStorageWriteable()) {
			throw new BackupException(R.string.ext_storage_not_writable, null);
		}

		File backupFile = getBackupFile();

		Properties props = config.toProperties();

		try {
			props.store(new FileWriter(backupFile), "Backup creation date:");
		} catch (IOException e) {
			throw new BackupException(R.string.error_while_loading_backup,
					backupFile.getAbsolutePath());
		}
	}

	public static void onRestore(Configuration config) throws BackupException {
		if (!isExternalStorageWriteable() && !isExternalStorageReadable()) {
			throw new BackupException(R.string.ext_storage_not_readable, null);
		}

		File backupFile = getBackupFile();

		Properties props = new Properties();
		try {
			props.load(new FileReader(backupFile));
		} catch (FileNotFoundException e) {
			throw new BackupException(R.string.backup_file_not_be_found,
					backupFile.getAbsolutePath());
		} catch (IOException e) {
			throw new BackupException(R.string.error_while_loading_backup,
					e.getLocalizedMessage());
		}

		config.loadProperties(props);
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static String getBackupFileName() {
		return getBackupFile().getAbsolutePath();
	}

	private static File getBackupFile() {
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				Constants.CONFIGURATION_BACKUP_FILE);
	}
}
