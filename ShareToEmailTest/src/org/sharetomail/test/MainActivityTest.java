/**
 * 
 */
package org.sharetomail.test;

import org.sharetomail.AddModifyEmailAddressActivity;
import org.sharetomail.MainActivity;
import org.sharetomail.SettingsActivity;
import org.sharetomail.util.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;

import com.robotium.solo.Solo;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mainActivity;
	private SharedPreferences sharedPreferences;

	private Solo solo;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mainActivity = getActivity();

		sharedPreferences = mainActivity.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

		clearSharedPreferences();

		solo = new Solo(getInstrumentation(), mainActivity);
	}

	private void clearSharedPreferences() {
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	public void testOpenNewEmail() {
		solo.clickOnText(mainActivity
				.getString(org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		solo.goBack();
	}

	public void testAddNewEmail_ValidEmail() {
		solo.clickOnButton(mainActivity
				.getString(org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example.org";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		assertEquals(testEmail, ((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getAdapter().getItem(0));
	}

	public void testAddNewEmail_ValidEmailWithoutTLD() {
		solo.clickOnButton(mainActivity
				.getString(org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.assertCurrentActivity("Current activity is not "
				+ MainActivity.class.getName(), MainActivity.class);

		assertEquals(testEmail, ((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getAdapter().getItem(0));
	}

	public void testAddNewEmail_InvalidEmail() {
		solo.clickOnButton(mainActivity
				.getString(org.sharetomail.R.string.add_email_address_button));

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
						mainActivity
								.getString(org.sharetomail.R.string.dialog_input_is_not_in_email_format),
						1, 1000));
	}

	public void testModifiedEmail() {
		solo.clickOnButton(mainActivity
				.getString(org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(AddModifyEmailAddressActivity.class, 10000);

		String testEmail = "test@example.org";
		String testModifiedEmail = "testModified@example.org";

		solo.enterText(
				(EditText) solo.getCurrentActivity().findViewById(
						org.sharetomail.R.id.emailAddressEditText), testEmail);

		solo.clickOnButton(solo.getCurrentActivity().getString(
				org.sharetomail.R.string.add_email_address_button));

		solo.waitForActivity(MainActivity.class, 10000);

		solo.clickLongOnView(((ListView) mainActivity
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getChildAt(0));
		solo.clickOnText(mainActivity
				.getString(org.sharetomail.R.string.modify_email_address_menu_item));

		solo.assertCurrentActivity("Current activity is not "
				+ AddModifyEmailAddressActivity.class.getName(),
				AddModifyEmailAddressActivity.class);

		assertEquals(testEmail, ((EditText) solo.getCurrentActivity()
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

	public void testOpenSettings() {
		solo.sendKey(Solo.MENU);
		solo.clickOnText(mainActivity
				.getString(org.sharetomail.R.string.action_settings));
		solo.assertCurrentActivity("Current activity is not "
				+ SettingsActivity.class.getName(), SettingsActivity.class);

		solo.goBack();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		clearSharedPreferences();
	}

}
