/**
 * 
 */
package org.sharetomail.test;

import org.sharetomail.AddModifyEmailAddressActivity;
import org.sharetomail.MainActivity;
import org.sharetomail.SettingsActivity;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
			throw new RuntimeException("Screen is locked! Please open it!");
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
				defaultEmail);

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

		assertEquals(testEmail, adapter.getItem(adapter.getCount() - 1));
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

		assertEquals(testEmail, adapter.getItem(adapter.getCount() - 1));
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

		assertEquals(testModifiedEmail, ((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getAdapter().getItem(0));
	}

	public void testDeleteEmail() {
		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.delete_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

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

		assertEquals(defaultEmail, sharedPreferences.getString(
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

		solo.clickLongOnView(((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(solo
				.getCurrentActivity()
				.getString(
						org.sharetomail.R.string.unset_as_default_email_address_menu_item));

		assertEquals("", sharedPreferences.getString(
				Constants.DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY, "N/A"));
	}

	public void testOpenSettings() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.action_settings));
		solo.assertCurrentActivity("Current activity is not "
				+ SettingsActivity.class.getName(), SettingsActivity.class);
	}

	public void testOpenAbout() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.action_about));

		solo.searchText(solo.getCurrentActivity().getResources()
				.getString(org.sharetomail.R.string.backup_config_button));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		clearSharedPreferences();
	}

}
