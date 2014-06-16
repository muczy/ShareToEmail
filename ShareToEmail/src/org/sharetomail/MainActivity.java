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
import org.sharetomail.util.DefaultItemHandlingAdapter;
import org.sharetomail.util.EmailAddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getName();

	private ListView emailAddressesListView;
	private DefaultItemHandlingAdapter<EmailAddress> emailAddressesAdapter;

	private Configuration config;
	private static Resources mResources;

	private EmailAddress selectedItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// TODO: remove this?
		getPreferences(MODE_PRIVATE);

		config = new Configuration(getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE));

		mResources = getResources();

		// If we have a default email address and the auto use option is set
		// then don't even prompt the user.
		if (!config.getDefaultEmailAddress().getEmailAddress().isEmpty()
				&& getIntent().hasExtra(Intent.EXTRA_TEXT)
				&& config.isAutoUseDefaultEmailAddress()) {
			sendEmail(config.getDefaultEmailAddress());

			return;
		}

		initWidgets();

		emailAddressesAdapter = new DefaultItemHandlingAdapter<EmailAddress>(
				this, config.getEmailAddresses(),
				config.getDefaultEmailAddress());
		emailAddressesListView.setAdapter(emailAddressesAdapter);

		Log.d("WTF", "config.getEmailAddresses(): "
				+ config.getEmailAddresses().size());
		Log.d("WTF",
				"emailAddressesListView child: "
						+ emailAddressesListView.getChildCount());
		Log.d("WTF", "adapter count: "
				+ emailAddressesListView.getAdapter().getCount());

	}

	public static Resources getResourcesObject() {
		return mResources;
	}

	private void initWidgets() {
		Button addEmailAddressButton = (Button) findViewById(R.id.addEmailAddressButton);
		emailAddressesListView = (ListView) findViewById(R.id.emailAddressesListView);

		// If the app has been started from a launcher (and not a share intent)
		// then don't react on short clicks.
		if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
			emailAddressesListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							sendEmail(emailAddressesAdapter.getItem(position));
						}
					});
		}

		registerForContextMenu(emailAddressesListView);
		emailAddressesListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						selectedItem = emailAddressesAdapter.getItem(position);
						return false;
					}
				});

		addEmailAddressButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent addEmailAddressIntent = new Intent(
						getApplicationContext(),
						AddModifyEmailAddressActivity.class);
				startActivityForResult(addEmailAddressIntent,
						Constants.ADD_EMAIL_ADDRESS_ACTIVITY_REQUEST_CODE);
			}
		});
	}

	private void sendEmail(EmailAddress emailAddress) {
		String textFromIntent = getIntent().getStringExtra(Intent.EXTRA_TEXT);

		String subjectFromIntent = getSubject(textFromIntent);

		if (emailAddress.getEmailAppPackageName() != null
				&& !emailAddress.getEmailAppPackageName().isEmpty()) {
			startSpecifiedEmailApp(emailAddress, subjectFromIntent,
					textFromIntent);
		} else {
			startEmailAppSelector(emailAddress, subjectFromIntent,
					textFromIntent);
		}
	}

	private void startEmailAppSelector(EmailAddress emailAddress,
			String subject, String text) {
		Intent sendMailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				Constants.MAILTO_SCHEME, emailAddress.getEmailAddress(), null));

		sendMailIntent.putExtra(Intent.EXTRA_SUBJECT,
				config.getEmailSubjectPrefix() + subject);
		sendMailIntent.putExtra(Intent.EXTRA_TEXT, text);

		startActivity(Intent.createChooser(sendMailIntent,
				getString(R.string.send_email)));

		finish();
	}

	private void startSpecifiedEmailApp(final EmailAddress emailAddress,
			String subject, String text) {
		Intent sendMailIntent = new Intent(Intent.ACTION_SENDTO);

		sendMailIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		sendMailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		sendMailIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { emailAddress.getEmailAddress() });
		sendMailIntent.putExtra(Intent.EXTRA_SUBJECT,
				config.getEmailSubjectPrefix() + subject);
		sendMailIntent.putExtra(Intent.EXTRA_TEXT, text);

		sendMailIntent.setClassName(emailAddress.getEmailAppPackageName(),
				emailAddress.getEmailAppName());

		try {
			startActivity(sendMailIntent);
		} catch (ActivityNotFoundException e) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.email_app_not_found);
			builder.setPositiveButton(R.string.modify_button,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent modifyEmailAddressIntent = new Intent(
									MainActivity.this,
									AddModifyEmailAddressActivity.class);
							modifyEmailAddressIntent.putExtra(
									Constants.ORIG_EMAIL_ADDRESS_INTENT_KEY,
									emailAddress);
							startActivityForResult(
									modifyEmailAddressIntent,
									Constants.MODIFY_EMAIL_ADDRESS_ACTIVITY_REQUEST_CODE);
						}
					});
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();

			return;
		}

		finish();
	}

	private String getSubject(String textFromIntent) {
		String subjectFromIntent = getIntent().getStringExtra(
				Intent.EXTRA_SUBJECT);
		// Some apps (e.g. Feedly or Digg) don't set EXTRA_SUBJECT but the
		// subject is included in the EXTRA_TEXT separated with a space from the
		// URL like "text test http://url" or "text test\nhttp://url".
		if (subjectFromIntent == null) {
			String[] splittedText = textFromIntent.split("\\s");
			String url = splittedText[splittedText.length - 1];
			int urlPos = textFromIntent.lastIndexOf(url);
			String subject = textFromIntent.substring(0, urlPos).trim();

			if (!subject.isEmpty()) {
				subjectFromIntent = subject;
			} else {
				subjectFromIntent = "";
			}
		}
		return subjectFromIntent;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// As Android API < Version 11 does not implement PopupMenu we use a
		// custom solution here.
		if (v == emailAddressesListView) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.email_address_popup_menu, menu);

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			// Change the context menu item "Set as default" to
			// "Unset as default" for the default selected list item.
			if (((TextView) info.targetView).getText().toString()
					.equals(config.getDefaultEmailAddress().getEmailAddress())) {
				menu.findItem(R.id.setAsDefaultEmailAddressItem).setVisible(
						false);
				menu.findItem(R.id.unsetAsDefaultEmailAddressItem).setVisible(
						true);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setAsDefaultEmailAddressItem:
			config.setDefaultEmailAddress(selectedItem);
			emailAddressesAdapter.setDefaultItem(selectedItem);
			return true;
		case R.id.unsetAsDefaultEmailAddressItem:
			config.clearDefaultEmailAddress();
			emailAddressesAdapter.setDefaultItem(null);
			return true;
		case R.id.modifyEmailAddressItem:
			Intent modifyEmailAddressIntent = new Intent(this,
					AddModifyEmailAddressActivity.class);
			modifyEmailAddressIntent.putExtra(
					Constants.ORIG_EMAIL_ADDRESS_INTENT_KEY, selectedItem);
			startActivityForResult(modifyEmailAddressIntent,
					Constants.MODIFY_EMAIL_ADDRESS_ACTIVITY_REQUEST_CODE);
			return true;
		case R.id.deleteEmailAddressItem:
			config.removeEmailAddress(selectedItem);

			if (config.getDefaultEmailAddress().equals(selectedItem)) {
				config.clearDefaultEmailAddress();
			}

			refreshEmailList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivityForResult(settingsIntent,
					Constants.SETTINGS_ACTIVITY_REQUEST_CODE);
			return true;
		case R.id.action_about:
			AlertDialog aboutDialog = createAboutDialog();
			aboutDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private AlertDialog createAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		final View aboutDialogView = inflater.inflate(R.layout.dialog_about,
				null);

		((TextView) aboutDialogView.findViewById(R.id.aboutVersionTextView))
				.setText(getString(R.string.about_text_version,
						getAppVersionName()));

		((TextView) aboutDialogView.findViewById(R.id.aboutAuthorTextView))
				.setText(getString(R.string.about_text_author,
						getString(R.string.about_text_author_name)));

		TextView aboutLogoUrlTextView = ((TextView) aboutDialogView
				.findViewById(R.id.aboutLogoUrlTextView));
		aboutLogoUrlTextView.setText(R.string.about_text_logo_artwork_url);
		Linkify.addLinks(aboutLogoUrlTextView, Linkify.ALL);

		builder.setView(aboutDialogView).setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		return builder.create();
	}

	private String getAppVersionName() {
		PackageInfo packageInfo = null;
		String versionName = "";

		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Exception while getting app version", e);
		}

		return versionName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refreshEmailList();

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refreshEmailList() {
		emailAddressesAdapter.clear();
		emailAddressesAdapter.addAll(config.getEmailAddresses());
		emailAddressesAdapter.setDefaultItem(config.getDefaultEmailAddress());
	}

}
