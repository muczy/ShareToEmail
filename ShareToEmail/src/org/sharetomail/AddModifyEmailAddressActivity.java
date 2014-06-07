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
import org.sharetomail.util.EmailAddress;
import org.sharetomail.util.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddModifyEmailAddressActivity extends Activity {

	protected static final String TAG = AddModifyEmailAddressActivity.class
			.getName();

	private Configuration config;
	private EmailAddress origEmail;
	private String emailAppName = "";
	private String emailAppPackageName = "";
	private Button emailAppButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_email_address);
		// Show the Up button in the action bar.
		setupActionBar();

		config = new Configuration(getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE));

		final Button addModifyEmailAddressButton = (Button) findViewById(R.id.addModifyEmailAddressButton);
		final TextView emailAddressTextView = (TextView) findViewById(R.id.emailAddressEditText);
		emailAppButton = (Button) findViewById(R.id.emailAppButton);

		// Custom text change listener to enable/disable the add/modify email
		// address button when the input text changes.
		emailAddressTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					addModifyEmailAddressButton.setEnabled(true);
				} else {
					addModifyEmailAddressButton.setEnabled(false);
				}
			}
		});

		addModifyEmailAddressButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check if the input is in email address format. If it is not
				// warn the user.
				String inputText = emailAddressTextView.getText().toString()
						.trim();
				if (Util.isInEmailFormat(inputText)) {
					EmailAddress emailAddress = new EmailAddress(inputText,
							emailAppName, emailAppPackageName);

					if (origEmail != null) {
						int position = -1;
						for (int i = 0; i < config.getEmailAddresses().size(); i++) {
							if (origEmail.equals(config.getEmailAddresses()
									.get(i))) {
								position = i;
								break;
							}
						}

						// Original email address was not found (somebody
						// removed it in the mean time?) so we add it.
						if (position < 0) {
							Log.w(TAG,
									"Email address \""
											+ origEmail.getEmailAddress()
											+ "\" was not found which is bad. Adding it.");
							config.addEmailAddress(emailAddress);
						} else {
							config.setEmailAddress(position, emailAddress);
						}
					} else {
						config.addEmailAddress(emailAddress);
					}

					setResult(Activity.RESULT_OK, getIntent());
					finish();
				} else {
					Toast.makeText(
							AddModifyEmailAddressActivity.this,
							getString(R.string.dialog_input_is_not_in_email_format),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		emailAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startEmailAppSelector();
			}
		});

		// If we get the email address in the intent then we modify it so fill
		// the TextView and rename labels.
		if (getIntent().hasExtra(Constants.ORIG_EMAIL_ADDRESS_INTENT_KEY)) {
			origEmail = getIntent().getParcelableExtra(
					Constants.ORIG_EMAIL_ADDRESS_INTENT_KEY);

			emailAppName = origEmail.getEmailAppName();
			emailAppPackageName = origEmail.getEmailAppPackageName();

			setTitle(R.string.title_activity_modify_email_address);
			addModifyEmailAddressButton
					.setText(R.string.modify_email_address_button);
			emailAddressTextView.setText(origEmail.getEmailAddress());

			setEmailAppButtonText();
		}
	}

	protected void startEmailAppSelector() {
		Intent emailAppSelectorActivity = new Intent(this,
				EmailAppSelectorActivity.class);
		startActivityForResult(emailAppSelectorActivity,
				Constants.EMAIL_APP_SELECTOR_ACTIVITY_REQUEST_CODE);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.EMAIL_APP_SELECTOR_ACTIVITY_REQUEST_CODE) {
			emailAppName = data
					.getStringExtra(Constants.EMAIL_APP_NAME_INTENT_KEY);
			emailAppPackageName = data
					.getStringExtra(Constants.EMAIL_APP_PACKAGE_NAME_INTENT_KEY);

			setEmailAppButtonText();
		}
	}

	private void setEmailAppButtonText() {
		CharSequence emailAppLabel = Util.getEmailAppLabel(emailAppPackageName,
				getPackageManager());

		// The originally selected app might got changed or uninstalled.
		if (emailAppLabel == null || emailAppLabel.length() == 0) {
			emailAppName = "";
			emailAppPackageName = "";
			emailAppLabel = getString(R.string.app_selector);
		}

		emailAppButton.setText(emailAppLabel);
	}

}
