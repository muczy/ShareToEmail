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

import org.sharetomail.util.Constants;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	private CheckBox autoUseDefaultEmailAddressCheckBox;
	private EditText emailSubjectPrefixEditText;

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

		// Set the Auto use default email address checkbox checked if the value
		// is true.
		if (getIntent().getBooleanExtra(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_INTENT_KEY, true)) {
			autoUseDefaultEmailAddressCheckBox.setChecked(true);
		}

		emailSubjectPrefixEditText.setText(emailSubjectPrefix);
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

}
