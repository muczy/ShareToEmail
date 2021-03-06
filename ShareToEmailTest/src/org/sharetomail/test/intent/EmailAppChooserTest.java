/**
 * 
 */
package org.sharetomail.test.intent;

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

public class EmailAppChooserTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TEST_SUBJECT = "test subject";
	private static final String TEST_LINK = "http://test.lnk";
	private static final String CHOOSER_APP_CLASS_NAME = "com.android.internal.app.ChooserActivity";

	private SharedPreferences sharedPreferences;

	private Solo solo;
	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\"\",\"EMAIL_APP_NAME\":\"\",\"EMAIL_ADDRESS\":\""
			+ defaultEmail + "\"}";

	public EmailAppChooserTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() {
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

	@Override
	public MainActivity getActivity() {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_TEXT, TEST_LINK);
		intent.putExtra(Intent.EXTRA_SUBJECT, TEST_SUBJECT);
		setActivityIntent(intent);
		return super.getActivity();
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

	public void testUseEmailAppChooser() throws InterruptedException {
		String subjectPrefix = solo.getCurrentActivity().getString(
				R.string.default_email_subject_prefix);
		solo.clickOnView(Util.getEmailAddressesListView(solo).getChildAt(0));

		solo.waitForActivity(CHOOSER_APP_CLASS_NAME, 5000);
		assertEquals(CHOOSER_APP_CLASS_NAME, solo.getCurrentActivity()
				.getClass().getName());

		Intent intentToStart = solo.getCurrentActivity().getIntent()
				.getParcelableExtra(Intent.EXTRA_INTENT);
		assertEquals(TEST_LINK, intentToStart.getStringExtra(Intent.EXTRA_TEXT));
		assertEquals(subjectPrefix + TEST_SUBJECT,
				intentToStart.getStringExtra(Intent.EXTRA_SUBJECT));
	}
}
