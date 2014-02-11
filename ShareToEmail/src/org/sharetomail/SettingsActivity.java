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

import org.sharetomail.util.Configuration;
import org.sharetomail.util.Constants;
import org.sharetomail.util.backup.BackupException;
import org.sharetomail.util.backup.ConfigurationBackupAgent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
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

	private Configuration config;

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

		config = new Configuration(getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE));

		initWidgets();
	}

	private void initWidgets() {
		autoUseDefaultEmailAddressCheckBox = (CheckBox) findViewById(R.id.autoUseDefaultEmailAddressCheckBox);
		emailSubjectPrefixEditText = (EditText) findViewById(R.id.emailSubjectPrefixEditText);
		backupConfigButton = (Button) findViewById(R.id.backupConfigButton);
		restoreConfigButton = (Button) findViewById(R.id.restoreConfigButton);

		initWidgetValues();

		backupConfigButton.setOnClickListener(this);
		restoreConfigButton.setOnClickListener(this);
	}

	private void initWidgetValues() {
		if (config.isAutoUseDefaultEmailAddress()) {
			autoUseDefaultEmailAddressCheckBox.setChecked(true);
		}

		emailSubjectPrefixEditText.setText(config.getEmailSubjectPrefix());
	}

	@Override
	public void onBackPressed() {
		// Get values from UI controls, put them into the intent and return.
		config.setAutoUseDefaultEmailAddress(autoUseDefaultEmailAddressCheckBox
				.isChecked());

		config.setEmailSubjectPrefix(emailSubjectPrefixEditText.getText()
				.toString());

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
		try {
			if (v == backupConfigButton) {
				ConfigurationBackupAgent.onBackup(config);
				Toast.makeText(
						this,
						getString(R.string.backup_file_created,
								ConfigurationBackupAgent.getBackupFileName()),
						Toast.LENGTH_LONG).show();
			} else if (v == restoreConfigButton) {
				ConfigurationBackupAgent.onRestore(config);
				Toast.makeText(
						this,
						getString(R.string.backup_file_loaded,
								ConfigurationBackupAgent.getBackupFileName()),
						Toast.LENGTH_LONG).show();
				initWidgetValues();
			}
		} catch (BackupException e) {
			Toast.makeText(this, e.getFormattedMessage(getResources()),
					Toast.LENGTH_LONG).show();
		}
	}

}
