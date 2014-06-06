/**
 * 
 */
package org.sharetomail.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.sharetomail.AddModifyEmailAddressActivity;
import org.sharetomail.EmailAppSelectorActivity;
import org.sharetomail.MainActivity;
import org.sharetomail.SettingsActivity;
import org.sharetomail.util.Configuration;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.robotium.solo.Solo;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private SharedPreferences sharedPreferences;

	private Solo solo;
	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\"\",\"EMAIL_APP_NAME\":\"\",\"EMAIL_ADDRESS\":\""
			+ defaultEmail + "\"}";

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		sharedPreferences = getInstrumentation()
				.getTargetContext()
				.getApplicationContext()
				.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE);

		clearSharedPreferences();

		addDefaultEmail();

		solo = new Solo(getInstrumentation(), getActivity());

		KeyguardManager myKM = (KeyguardManager) getActivity()
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (myKM.inKeyguardRestrictedInputMode()) {
			fail("Screen is locked! Please open it!");
		}
	}

	private void clearSharedPreferences() {
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	private void addDefaultEmail() {
		Editor editor = sharedPreferences.edit();

		editor.putString(Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				defaultEmailConfigLine);

		editor.commit();
	}

	public void testOpenNewEmail() {
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);
	}

	public void testAddNewEmail_ValidEmail() {
		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example.org";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		ListAdapter adapter = ((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getAdapter();

		assertEquals(testEmail,
				String.valueOf(adapter.getItem(adapter.getCount() - 1)));
	}

	public void testAddNewEmail_ValidEmailWithoutTLD() {
		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		ListAdapter adapter = ((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getAdapter();

		assertEquals(testEmail,
				String.valueOf(adapter.getItem(adapter.getCount() - 1)));
	}

	public void testAddNewEmail_InvalidEmail() {
		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertTrue(
				"Invalid email format Toast not found!",
				solo.waitForText(
						solo.getCurrentActivity()
								.getString(
										org.sharetomail.R.string.dialog_input_is_not_in_email_format),
						1, 1000));
	}

	public void testAddNewEmail_WithSpecificEmailApp() {
		// Add test email address.
		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example.org";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		// Set an email app for the test email address.
		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.app_selector));

		solo.waitForActivity(EmailAppSelectorActivity.class, 10000);

		ListView emailAppListView = (ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAppListView);

		if (emailAppListView.getChildCount() < 2) {
			fail("No email apps were found. Please install at least one!");
		}

		int selectedAppPosition = 1;
		String selectedApp = String.valueOf(emailAppListView.getAdapter()
				.getItem(selectedAppPosition));
		solo.clickOnView(emailAppListView.getChildAt(selectedAppPosition));
		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		// Save the test email address.
		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		// Open email address modification activity and verify email app.
		ListView addressListView = (ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView);
		solo.clickLongOnView(addressListView.getChildAt(addressListView
				.getAdapter().getCount() - 1));

		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.modify_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertEquals(selectedApp, String.valueOf(solo.getButton(1).getText()));
	}

	public void testModifiedEmail() {
		String testModifiedEmail = "testModified@example.org";

		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.modify_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertEquals(defaultEmail, ((EditText) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressEditText))
				.getText().toString());

		solo.clearEditText((EditText) solo.getCurrentActivity().findViewById(
				org.sharetomail.R.id.emailAddressEditText));
		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText),
				testModifiedEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.modify_email_address_button));

		solo.waitForActivity(MainActivity.class, 10000);

		assertEquals(testModifiedEmail, String.valueOf(((ListView) solo
				.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressesListView))
				.getAdapter().getItem(0)));
	}

	public void testDeleteEmail() {
		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.delete_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		solo.waitForView(((ListView) solo.getCurrentActivity().findViewById(
				org.sharetomail.R.id.emailAddressesListView)));

		assertEquals(
				0,
				((ListView) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressesListView))
						.getAdapter().getCount());
	}

	public void testSetDefaultEmail() {
		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo
				.getCurrentActivity()
				.getString(
						org.sharetomail.R.string.set_as_default_email_address_menu_item));

		assertEquals(defaultEmailConfigLine, sharedPreferences.getString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "N/A"));
	}

	public void testUnsetDefaultEmail() {
		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo
				.getCurrentActivity()
				.getString(
						org.sharetomail.R.string.set_as_default_email_address_menu_item));

		solo.waitForActivity(MainActivity.class, 2000);

		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo
				.getCurrentActivity()
				.getString(
						org.sharetomail.R.string.unset_as_default_email_address_menu_item));

		assertFalse(sharedPreferences
				.contains(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY));
	}

	public void testOpenAbout() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.action_about));
		assertTrue(solo.waitForDialogToOpen());
	}

	public void testOpenSettings() {
		openSettings();
	}

	private void openSettings() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.action_settings));
		solo.assertCurrentActivity("Current activity is not "
				+ SettingsActivity.class.getName(), SettingsActivity.class);
	}

	public void testSettings_AutoUseDefaultEmailAddress() {
		openSettings();

		solo.clickOnCheckBox(0);

		solo.goBack();
		solo.goBack();

		assertFalse(sharedPreferences
				.getBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						true));

		openSettings();

		solo.clickOnCheckBox(0);

		solo.goBack();
		solo.goBack();

		assertTrue(sharedPreferences
				.getBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						false));
	}

	public void testSettings_EmailSubjectPrefix() {
		openSettings();

		EditText subjectPrefixEditText = (EditText) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailSubjectPrefixEditText);

		solo.clearEditText(subjectPrefixEditText);
		String testsubjectPrefix = "test email subject prefix";
		solo.enterText(subjectPrefixEditText, testsubjectPrefix);

		solo.goBack();

		assertEquals(testsubjectPrefix, sharedPreferences.getString(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY, ""));
	}

	public void testSettings_Backup() {
		Editor editor = sharedPreferences.edit();

		editor.putString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				defaultEmailConfigLine);
		editor.putString(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY);
		editor.putBoolean(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				false);

		editor.commit();

		openSettings();

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.backup_config_button));

		File backupFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				Constants.CONFIGURATION_BACKUP_FILE);
		assertTrue(backupFile.canRead());

		Properties props = new Properties();
		try {
			props.load(new FileReader(backupFile));
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		Properties propsFromConfig = new Configuration(sharedPreferences)
				.toProperties();

		assertEquals(propsFromConfig, props);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		clearSharedPreferences();
	}

}
