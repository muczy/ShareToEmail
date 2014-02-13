package org.sharetomail.util.backup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.sharetomail.R;
import org.sharetomail.util.Configuration;
import org.sharetomail.util.Constants;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

public class ConfigurationBackupAgent extends BackupAgent {
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

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {
		Configuration config = new Configuration(getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE));

		// Create buffer stream and data output stream for our data
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		DataOutputStream outWriter = new DataOutputStream(bufStream);

		// Write structured data
		writeBackupToStream(config, outWriter);

		// Send the data to the Backup Manager via the BackupDataOutput
		byte[] buffer = bufStream.toByteArray();
		int len = buffer.length;
		data.writeEntityHeader(Constants.SHARETOEMAIL_BACKUP_KEY, len);
		data.writeEntityData(buffer, len);
	}

	public void writeBackupToStream(Configuration config,
			DataOutputStream outWriter) throws IOException {
		outWriter.writeUTF(config.getEmailAddressesAsString());
		outWriter.writeUTF(config.getEmailSubjectPrefix());
		outWriter.writeUTF(config.getDefaultEmailAddress());
		outWriter.writeBoolean(config.isAutoUseDefaultEmailAddress());
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {
		Configuration config = new Configuration(getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE));

		// There should be only one entity, but the safest
		// way to consume it is using a while loop
		while (data.readNextHeader()) {
			String key = data.getKey();
			int dataSize = data.getDataSize();

			// If the key is ours (for saving top score). Note this key was used
			// when
			// we wrote the backup entity header
			if (Constants.SHARETOEMAIL_BACKUP_KEY.equals(key)) {
				// Create an input stream for the BackupDataInput
				byte[] dataBuf = new byte[dataSize];
				data.readEntityData(dataBuf, 0, dataSize);
				ByteArrayInputStream baStream = new ByteArrayInputStream(
						dataBuf);
				DataInputStream in = new DataInputStream(baStream);

				config.setEmailAddressesFromString(in.readUTF());
				config.setEmailSubjectPrefix(in.readUTF());
				config.setDefaultEmailAddress(in.readUTF());
				config.setAutoUseDefaultEmailAddress(in.readBoolean());
			} else {
				// We don't know this entity key. Skip it. (Shouldn't happen.)
				data.skipEntityData();
			}
		}

		// Finally, write to the state blob (newState) that describes the
		// restored data
		FileOutputStream outstream = new FileOutputStream(
				newState.getFileDescriptor());
		DataOutputStream out = new DataOutputStream(outstream);
		writeBackupToStream(config, out);
	}
}
