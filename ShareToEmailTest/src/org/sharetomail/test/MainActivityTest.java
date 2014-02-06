/**
 * 
 */
package org.sharetomail.test;

import org.sharetomail.AddModifyEmailAddressActivity;
import org.sharetomail.MainActivity;
import org.sharetomail.SettingsActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.ActivityUnitTestCase;
import android.widget.Button;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

	private MainActivity mainActivity;
	private Button addEmailAddressButton;
	private SharedPreferences sharedPreferences;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				MainActivity.class);
		startActivity(intent, null, null);
		mainActivity = getActivity();

		addEmailAddressButton = (Button) mainActivity
				.findViewById(org.sharetomail.R.id.addEmailAddressButton);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mainActivity);

		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	public void testAddNewEmailClick() {
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				addEmailAddressButton.performClick();
				Intent triggeredIntent = MainActivityTest.this
						.getStartedActivityIntent();
				assertNotNull("Intent was null", triggeredIntent);
				assertTrue(
						"Intent was not instance of "
								+ AddModifyEmailAddressActivity.class.getName(),
						AddModifyEmailAddressActivity.class.getName().equals(
								triggeredIntent.getComponent().getClassName()));
			}
		});
	}

	public void testSettingsClick() {
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				getInstrumentation().invokeMenuActionSync(mainActivity,
						org.sharetomail.R.menu.settings, 0);
				getInstrumentation().waitForIdleSync();

				Intent triggeredIntent = MainActivityTest.this
						.getStartedActivityIntent();
				assertNotNull("Intent was null", triggeredIntent);
				assertTrue(
						"Intent was not instance of "
								+ SettingsActivity.class.getName(),
						SettingsActivity.class.getName().equals(
								triggeredIntent.getComponent().getClassName()));
			}
		});
	}
}
