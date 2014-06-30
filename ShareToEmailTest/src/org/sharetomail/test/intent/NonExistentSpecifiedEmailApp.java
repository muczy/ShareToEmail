package org.sharetomail.test.intent;

import org.sharetomail.AddModifyEmailAddressActivity;
import org.sharetomail.MainActivity;
import org.sharetomail.R;
import org.sharetomail.test.Util;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Window;
import android.view.WindowManager;

import com.robotium.solo.Solo;

public class NonExistentSpecifiedEmailApp extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TEST_SUBJECT = "test subject";
	private static final String TEST_LINK = "http://test.lnk";

	private SharedPreferences sharedPreferences;

	private Solo solo;
	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\"testAppPackage\",\"EMAIL_APP_NAME\":\"testAppName\",\"EMAIL_ADDRESS\":\""
			+ defaultEmail + "\"}";

	public NonExistentSpecifiedEmailApp() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.putExtra(Intent.EXTRA_TEXT, TEST_LINK);
		intent.putExtra(Intent.EXTRA_SUBJECT, TEST_SUBJECT);
		setActivityIntent(intent);

		sharedPreferences = getInstrumentation()
				.getTargetContext()
				.getApplicationContext()
				.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE);

		clearSharedPreferences();

		addDefaultEmail();

		solo = new Solo(getInstrumentation());

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

	private void clickOnEmailAddressAndWaitForErrorDialog() {
		String emailAppNotFountText = solo.getCurrentActivity().getString(
				R.string.email_app_not_found);
		solo.clickOnView(Util.getEmailAddressesListView(solo).getChildAt(0));

		assertTrue(solo.waitForText(emailAppNotFountText));
	}

	public void testNonExistentSpecifiedEmailApp_Modify()
			throws InterruptedException {
		clickOnEmailAddressAndWaitForErrorDialog();

		solo.clickOnButton(solo.getCurrentActivity().getString(
				R.string.modify_button));

		assertTrue(solo.waitForActivity(AddModifyEmailAddressActivity.class,
				5000));
	}

	public void testNonExistentSpecifiedEmailApp_Cancel()
			throws InterruptedException {
		clickOnEmailAddressAndWaitForErrorDialog();

		solo.clickOnButton(solo.getCurrentActivity().getString(
				android.R.string.cancel));

		assertTrue(solo.waitForActivity(MainActivity.class, 5000));
	}
}
