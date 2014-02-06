/*******************************************************************************
 * Copyright 2013 Peter Mihaly Avramucz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.sharetomail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.sharetomail.util.Constants;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {

	private CheckBox autoUseDefaultEmailAddressCheckBox;
	private EditText emailSubjectPrefixEditText;
	private Button backupConfigButton;
	private Button restoreConfigButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();

		// Get the email subject prefix from the intent and set it to default if
		// it's not set.
		String emailSubjectPrefix = getIntent().getStringExtra(
				Constants.EMAIL_SUBJECT_INTENT_KEY);
		if (emailSubjectPrefix == null) {
			emailSubjectPrefix = getString(R.string.default_email_subject_prefix);
		}

		autoUseDefaultEmailAddressCheckBox = (CheckBox) findViewById(R.id.autoUseDefaultEmailAddressCheckBox);
		emailSubjectPrefixEditText = (EditText) findViewById(R.id.emailSubjectPrefixEditText);
		backupConfigButton = (Button) findViewById(R.id.backupConfigButton);
		restoreConfigButton = (Button) findViewById(R.id.restoreConfigButton);

		// Set the Auto use default email address checkbox checked if the value
		// is true.
		if (getIntent().getBooleanExtra(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_INTENT_KEY, true)) {
			autoUseDefaultEmailAddressCheckBox.setChecked(true);
		}

		emailSubjectPrefixEditText.setText(emailSubjectPrefix);

		backupConfigButton.setOnClickListener(this);
		restoreConfigButton.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		// Get values from UI controls, put them into the intent and return.

		if (autoUseDefaultEmailAddressCheckBox.isChecked()) {
			getIntent().putExtra(
					Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_INTENT_KEY, true);
		} else {
			getIntent().putExtra(
					Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_INTENT_KEY, false);
		}

		getIntent().putExtra(Constants.EMAIL_SUBJECT_INTENT_KEY,
				emailSubjectPrefixEditText.getText().toString());

		setResult(Activity.RESULT_OK, getIntent());
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v == backupConfigButton) {
			backupConfiguration();
		} else if (v == restoreConfigButton) {
			restoreConfiguration();
		}
	}

	private void backupConfiguration() {
		if (!isExternalStorageWriteable()) {
			Toast.makeText(this, R.string.ext_storage_not_writable,
					Toast.LENGTH_LONG).show();
			return;
		}

		File backupFile = getBackupFile();

		Properties props = new Properties();

		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

		props.put(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				sharedPreferences.getString(
						Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, ""));
		props.put(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				sharedPreferences.getString(
						Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						""));
		props.put(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				sharedPreferences.getString(
						Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
						""));
		props.put(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				String.valueOf(sharedPreferences
						.getBoolean(
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
								true)));

		try {
			props.store(new FileWriter(backupFile), "Backup creation date:");
		} catch (IOException e) {
			Toast.makeText(
					this,
					getString(R.string.error_while_loading_backup,
							e.getMessage()), Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(
				this,
				getString(R.string.backup_file_created,
						backupFile.getAbsolutePath()), Toast.LENGTH_LONG)
				.show();
	}

	private void restoreConfiguration() {
		if (!isExternalStorageWriteable() && !isExternalStorageReadable()) {
			Toast.makeText(this, R.string.ext_storage_not_readable,
					Toast.LENGTH_LONG).show();
			return;
		}

		File backupFile = getBackupFile();

		Properties props = new Properties();
		try {
			props.load(new FileReader(backupFile));
		} catch (FileNotFoundException e) {
			Toast.makeText(
					this,
					getString(R.string.backup_file_not_be_found,
							backupFile.getParent()), Toast.LENGTH_LONG).show();
			return;
		} catch (IOException e) {
			Toast.makeText(
					this,
					getString(R.string.error_while_loading_backup,
							e.getMessage()), Toast.LENGTH_LONG).show();
			return;
		}

		SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE)
				.edit();

		editor.putString(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				props.getProperty(
						Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY, ""));
		editor.putString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				props.getProperty(
						Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						""));
		editor.putString(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				props.getProperty(
						Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
						""));
		editor.putBoolean(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				Boolean.valueOf(props
						.getProperty(
								Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
								"true")));

		editor.commit();

		Toast.makeText(
				this,
				getString(R.string.backup_file_loaded,
						backupFile.getAbsolutePath()), Toast.LENGTH_LONG)
				.show();
	}

	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	private File getBackupFile() {
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				Constants.CONFIGURATION_BACKUP_FILE);
	}
}
