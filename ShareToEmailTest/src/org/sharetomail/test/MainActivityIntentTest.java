/**
 * 
 */
package org.sharetomail.test;

import org.sharetomail.MainActivity;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.robotium.solo.Solo;

public class MainActivityIntentTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private SharedPreferences sharedPreferences;

	private Solo solo;
	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\"\",\"EMAIL_APP_NAME\":\"\",\"EMAIL_ADDRESS\":\""
			+ defaultEmail + "\"}";

	public MainActivityIntentTest() {
		super(MainActivity.class);
	}

	protected void setupWithIntent(Intent intent) {
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.putExtra(Intent.EXTRA_TEXT, "http://test.lnk");
		// intent.putExtra(Intent.EXTRA_SUBJECT, "test subject");
		setActivityIntent(intent);

		sharedPreferences = getInstrumentation()
				.getTargetContext()
				.getApplicationContext()
				.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE);

		clearSharedPreferences();

		addDefaultEmail();

		solo = new Solo(getInstrumentation());

	}

	private void unlockScreen() {
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

	public void testAddNewEmail_ValidEmail() throws InterruptedException {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.putExtra(Intent.EXTRA_TEXT, "http://test.lnk");
		intent.putExtra(Intent.EXTRA_SUBJECT, "test subject");
		setupWithIntent(intent);
		unlockScreen();

		solo.clickOnView(getEmailAddressesListView().getChildAt(0));

		Thread.sleep(1000);

		assertEquals("test subject", solo.getCurrentActivity().getIntent()
				.getStringExtra(Intent.EXTRA_SUBJECT));
	}

	private View findViewById(int id) {
		return solo.getCurrentActivity().findViewById(id);
	}

	private ListView getEmailAddressesListView() {
		solo.waitForView(org.sharetomail.R.id.emailAddressesListView, 1, 2000);
		return (ListView) findViewById(org.sharetomail.R.id.emailAddressesListView);
	}
}
