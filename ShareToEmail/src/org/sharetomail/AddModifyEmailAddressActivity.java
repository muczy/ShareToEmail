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

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddModifyEmailAddressActivity extends Activity {

	private Configuration config;
	private String origEmail = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_email_address);
		// Show the Up button in the action bar.
		setupActionBar();

		config = new Configuration(getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE));

		final Button addModifyEmailAddressButton = (Button) findViewById(R.id.addModifyEmailAddressButton);
		final TextView emailAddressTextView = (TextView) findViewById(R.id.emailAddressTextView);

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
				// NOTE: The default Android Patterns.EMAIL_ADDRESS does not
				// seems to be handling user@host (host without TLD) so we try
				// to workaround this here by appending a ".com" string.
				String inputText = emailAddressTextView.getText().toString()
						.trim();
				if (isInEmailFormat(inputText)
						|| isInEmailFormat(inputText + ".com")) {
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
							config.addEmailAddress(inputText);
						}

						config.setEmailAddress(position, inputText);
					} else {
						config.addEmailAddress(inputText);
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

			private boolean isInEmailFormat(String text) {
				return Patterns.EMAIL_ADDRESS.matcher(text).matches();
			}
		});

		// If we get the email address in the intent then we modify it so fill
		// the TextView and rename labels.
		if (getIntent().hasExtra(Constants.ORIG_EMAIL_ADDRESS_INTENT_KEY)) {
			setTitle(R.string.title_activity_modify_email_address);
			addModifyEmailAddressButton
					.setText(R.string.modify_email_address_button);
			origEmail = getIntent().getStringExtra(
					Constants.ORIG_EMAIL_ADDRESS_INTENT_KEY);
			emailAddressTextView.setText(origEmail);
		}
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
		getMenuInflater().inflate(R.menu.add_email_address, menu);
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
