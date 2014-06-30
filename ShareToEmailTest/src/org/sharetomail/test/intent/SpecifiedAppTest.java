/**
 * 
 */
package org.sharetomail.test.intent;

import java.util.List;

import org.sharetomail.MainActivity;
import org.sharetomail.R;
import org.sharetomail.test.Util;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.robotium.solo.Solo;

public class SpecifiedAppTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TEST_SUBJECT = "test subject";
	private static final String TEST_LINK = "http://test.lnk";
	private static final String EMAIL_APP_NAME = "org.sharetomail.util.DummyTestActivity";
	private static final String EMAIL_APP_PKG_NAME = "org.sharetomail.util";

	private SharedPreferences sharedPreferences;

	private Solo solo;

	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\""
			+ EMAIL_APP_PKG_NAME + "\",\"EMAIL_APP_NAME\":\"" + EMAIL_APP_NAME
			+ "\",\"EMAIL_ADDRESS\":\"" + defaultEmail + "\"}";

	public SpecifiedAppTest() {
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

	@Override
	public MainActivity getActivity() {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_TEXT, TEST_LINK);
		intent.putExtra(Intent.EXTRA_SUBJECT, TEST_SUBJECT);
		setActivityIntent(intent);
		return super.getActivity();
	}

	public void testUseSpecifiedApp() throws InterruptedException {
		String subjectPrefix = solo.getCurrentActivity().getString(
				R.string.default_email_subject_prefix);
		solo.clickOnView(Util.getEmailAddressesListView(solo).getChildAt(0));
		Intent sendMailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE,
				Uri.fromParts(Constants.MAILTO_SCHEME, "", null));
		List<ResolveInfo> t = solo
				.getCurrentActivity()
				.getPackageManager()
				.queryIntentActivities(sendMailIntent,
						PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : t) {
			Log.d("WTF", resolveInfo.activityInfo.processName);
		}
		Thread.sleep(1000);

		fail(solo.getCurrentActivity().getClass().getName());

		solo.waitForActivity(EMAIL_APP_NAME, 5000);
		assertEquals(EMAIL_APP_NAME, solo.getCurrentActivity().getClass()
				.getName());

		Intent intentToStart = solo.getCurrentActivity().getIntent()
				.getParcelableExtra(Intent.EXTRA_INTENT);
		assertEquals(TEST_LINK, intentToStart.getStringExtra(Intent.EXTRA_TEXT));
		assertEquals(subjectPrefix + TEST_SUBJECT,
				intentToStart.getStringExtra(Intent.EXTRA_SUBJECT));
	}
}
