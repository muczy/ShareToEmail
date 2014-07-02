/**
 * 
 */
package org.sharetomail.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.sharetomail.R;
import org.sharetomail.AddModifyEmailAddressActivity;
import org.sharetomail.EmailAppSelectorActivity;
import org.sharetomail.MainActivity;
import org.sharetomail.SettingsActivity;
import org.sharetomail.util.Configuration;
import org.sharetomail.util.Constants;
import org.sharetomail.util.EmailAddress;
import org.sharetomail.util.backup.ConfigurationBackupAgent;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

		File backupFile = new File(ConfigurationBackupAgent.getBackupFileName());
		if (backupFile.exists()) {
			backupFile.delete();
		}

		KeyguardManager myKM = (KeyguardManager) getActivity()
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (myKM.inKeyguardRestrictedInputMode()) {
			final Window win = getActivity().getWindow();
			win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		}
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		clearSharedPreferences();

		super.tearDown();
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

	private EmailAddress getEmailAddressFromConfig(String emailAddress) {
		Configuration config = new Configuration(sharedPreferences);
		EmailAddress foundEmailAddress = null;

		for (EmailAddress emailAddressFromConfig : config.getEmailAddresses()) {
			if (emailAddressFromConfig.getEmailAddress().equals(emailAddress)) {
				foundEmailAddress = emailAddressFromConfig;
			}
		}

		return foundEmailAddress;
	}

	private boolean emailEmailAddressExistsInConfig(String emailAddress) {
		return getEmailAddressFromConfig(emailAddress) != null;
	}

	public void testOpenNewEmail() {
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);
	}

	public void testAddNewEmail_ValidEmail() {
		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example.org";

		solo.enterText(
				(EditText) Util.findViewById(solo, R.id.emailAddressEditText),
				testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		ListAdapter adapter = Util.getEmailAddressesListView(solo).getAdapter();

		assertEquals(testEmail,
				String.valueOf(adapter.getItem(adapter.getCount() - 1)));
		assertTrue(emailEmailAddressExistsInConfig(testEmail));
	}

	public void testAddNewEmail_ValidEmailWithoutTLD() {
		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example";

		solo.enterText(
				(EditText) Util.findViewById(solo, R.id.emailAddressEditText),
				testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		ListAdapter adapter = Util.getEmailAddressesListView(solo).getAdapter();

		assertEquals(testEmail,
				String.valueOf(adapter.getItem(adapter.getCount() - 1)));
		assertTrue(emailEmailAddressExistsInConfig(testEmail));
	}

	public void testAddNewEmail_InvalidEmail() {
		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test";

		solo.enterText(
				(EditText) Util.findViewById(solo, R.id.emailAddressEditText),
				testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertTrue("Invalid email format Toast not found!", solo.waitForText(
				solo.getCurrentActivity().getString(
						R.string.dialog_input_is_not_in_email_format), 1, 1000));
		assertFalse(emailEmailAddressExistsInConfig(testEmail));
	}

	public void testAddNewEmail_WithSpecificEmailApp_AppChooser()
			throws InterruptedException {
		specificEmailAppTest(0);
	}

	public void testAddNewEmail_WithSpecificEmailApp_NonAppChooser()
			throws InterruptedException {
		specificEmailAppTest(1);
	}

	private void specificEmailAppTest(int appSelectorPosition)
			throws InterruptedException {
		// Add test email address.
		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example.org";

		solo.enterText(
				(EditText) Util.findViewById(solo, R.id.emailAddressEditText),
				testEmail);

		// Set an email app for the test email address.
		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.app_chooser));

		solo.waitForActivity(EmailAppSelectorActivity.class, 10000);

		solo.waitForView(R.id.aboutAuthorTextView, 1, 1000);
		ListView emailAppListView = (ListView) solo.getCurrentActivity()
				.findViewById(R.id.emailAppListView);

		if (emailAppListView.getChildCount() < 2) {
			fail("No email apps were found. Please install at least one!");
		}

		String selectedApp = String.valueOf(((TextView) emailAppListView
				.getChildAt(appSelectorPosition).findViewById(
						R.id.emailAppTitleTextView)).getText());

		// On emulator there the default "Unsupported action" which has an
		// alternative label "Fallback".
		if (selectedApp.equals("Unsupported action")) {
			selectedApp = "Fallback";
		}

		solo.clickOnView(emailAppListView.getChildAt(appSelectorPosition));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		assertNotNull(solo.getButton(selectedApp));

		// Save the test email address.
		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		assertTrue(emailEmailAddressExistsInConfig(testEmail));
		EmailAddress emailAddressFromConfig = getEmailAddressFromConfig(testEmail);
		assertTrue(emailAddressFromConfig.getEmailAppName() != null
				|| !emailAddressFromConfig.getEmailAppName().isEmpty());
		assertTrue(emailAddressFromConfig.getEmailAppPackageName() != null
				|| !emailAddressFromConfig.getEmailAppPackageName().isEmpty());

		// Open email address modification activity and verify email app.
		ListView addressListView = Util.getEmailAddressesListView(solo);
		Thread.sleep(500);
		solo.clickLongOnView(addressListView.getChildAt(addressListView
				.getAdapter().getCount() - 1));

		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.modify_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertNotNull(solo.getButton(selectedApp));
	}

	public void testModifiedEmail() {
		String testModifiedEmail = "testModified@example.org";

		solo.clickLongOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.modify_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertEquals(defaultEmail, ((EditText) solo.getCurrentActivity()
				.findViewById(R.id.emailAddressEditText)).getText().toString());

		solo.clearEditText((EditText) Util.findViewById(solo,
				R.id.emailAddressEditText));
		solo.enterText(
				(EditText) Util.findViewById(solo, R.id.emailAddressEditText),
				testModifiedEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.modify_email_address_button));

		solo.waitForActivity(MainActivity.class, 10000);

		assertEquals(
				testModifiedEmail,
				String.valueOf(Util.getEmailAddressesListView(solo)
						.getAdapter().getItem(0)));
		assertNull(getEmailAddressFromConfig(defaultEmail));
		assertNotNull(getEmailAddressFromConfig(testModifiedEmail));
	}

	public void testDeleteEmail() {
		solo.clickLongOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.delete_email_address_menu_item));

		assertTrue(solo.searchText(solo.getCurrentActivity().getString(
				R.string.delete_email_address_question, defaultEmail)));

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.delete_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		solo.waitForView(Util.getEmailAddressesListView(solo));

		assertEquals(0, Util.getEmailAddressesListView(solo).getAdapter()
				.getCount());
		assertFalse(emailEmailAddressExistsInConfig(defaultEmail));
	}

	public void testDeleteEmail_Cancel() {
		solo.clickLongOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.delete_email_address_menu_item));

		assertTrue(solo.searchText(solo.getCurrentActivity().getString(
				R.string.delete_email_address_question, defaultEmail)));

		solo.clickOnButton(solo.getCurrentActivity().getString(
				android.R.string.cancel));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		solo.waitForView(Util.getEmailAddressesListView(solo));

		assertEquals(1, Util.getEmailAddressesListView(solo).getAdapter()
				.getCount());
		assertTrue(emailEmailAddressExistsInConfig(defaultEmail));
	}

	public void testSetDefaultEmail() throws InterruptedException {
		solo.clickLongOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.set_as_default_email_address_menu_item));

		Thread.sleep(500);

		assertEquals(defaultEmailConfigLine, sharedPreferences.getString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "N/A"));
	}

	public void testUnsetDefaultEmail() throws InterruptedException {
		solo.clickLongOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.set_as_default_email_address_menu_item));

		solo.waitForActivity(MainActivity.class, 2000);

		solo.clickLongOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.unset_as_default_email_address_menu_item));

		Thread.sleep(500);
		assertFalse(sharedPreferences
				.contains(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY));
	}

	public void testOpenAbout() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.action_about));
		assertTrue(solo.waitForDialogToOpen());
	}

	public void testOpenSettings() {
		openSettings();
	}

	private void openSettings() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(
				R.string.action_settings));
		solo.assertCurrentActivity("Current activity is not "
				+ SettingsActivity.class.getName(), SettingsActivity.class);
	}

	public void testSettings_AutoUseDefaultEmailAddress() {
		openSettings();

		solo.clickOnView(Util.findViewById(solo,
				R.id.autoUseDefaultEmailAddressCheckBox));

		solo.goBack();

		assertFalse(sharedPreferences
				.getBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						true));

		openSettings();

		solo.clickOnView(Util.findViewById(solo,
				R.id.autoUseDefaultEmailAddressCheckBox));

		solo.goBack();

		assertTrue(sharedPreferences
				.getBoolean(
						Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
						false));
	}

	public void testSettings_DebugLogEnabled() {
		openSettings();

		solo.clickOnView(Util.findViewById(solo, R.id.debugLogEnabledCheckBox));

		solo.goBack();

		assertTrue(sharedPreferences.getBoolean(
				Constants.DEBUG_LOG_ENABLED_SHARED_PREFERENCES_KEY, false));

		openSettings();

		solo.clickOnView(Util.findViewById(solo, R.id.debugLogEnabledCheckBox));

		solo.goBack();

		assertFalse(sharedPreferences.getBoolean(
				Constants.DEBUG_LOG_ENABLED_SHARED_PREFERENCES_KEY, true));
	}

	public void testSettings_EmailSubjectPrefix() {
		openSettings();

		EditText subjectPrefixEditText = (EditText) solo.getCurrentActivity()
				.findViewById(R.id.emailSubjectPrefixEditText);

		solo.clearEditText(subjectPrefixEditText);
		String testsubjectPrefix = "test email subject prefix";
		solo.enterText(subjectPrefixEditText, testsubjectPrefix);

		solo.goBack();

		assertEquals(testsubjectPrefix, sharedPreferences.getString(
				Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY, ""));
	}

	public void testSettings_Backup() throws InterruptedException {
		assertTrue("External storage is not available! Please insert SDCARD!",
				ConfigurationBackupAgent.isExternalStorageWriteable());

		openSettings();

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.backup_config_button));

		File backupFile = new File(ConfigurationBackupAgent.getBackupFileName());
		Thread.sleep(2000);
		assertTrue(backupFile.exists());
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

		Thread.sleep(10000);
		assertEquals(propsFromConfig, props);
	}

	public void testSettings_Restore() throws IOException {
		assertTrue(
				"External storage is not available! Please insert SDCARD!",
				ConfigurationBackupAgent.isExternalStorageReadable()
						|| ConfigurationBackupAgent
								.isExternalStorageWriteable());

		File backupFile = new File(ConfigurationBackupAgent.getBackupFileName());

		Properties props = new Properties();

		props.put(Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				defaultEmailConfigLine);
		String testEmailSubjectPrefix = "testEmailSubjectPrefix";
		props.put(Constants.EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY,
				testEmailSubjectPrefix);
		boolean testUseDefaultEmail = true;
		props.put(
				Constants.AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY,
				Boolean.toString(testUseDefaultEmail));

		props.store(new FileWriter(backupFile, false), "");

		solo.waitForActivity(MainActivity.class, 2000);

		assertEquals(defaultEmail,
				((TextView) Util.getEmailAddressesListView(solo).getChildAt(0))
						.getText().toString());

		openSettings();

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.restore_config_button));

		solo.goBack();

		openSettings();

		assertEquals(testEmailSubjectPrefix, ((EditText) Util.findViewById(
				solo, R.id.emailSubjectPrefixEditText)).getText().toString());
		assertEquals(
				testUseDefaultEmail,
				((CheckBox) solo.getCurrentActivity().findViewById(
						R.id.autoUseDefaultEmailAddressCheckBox)).isChecked());
	}
}
