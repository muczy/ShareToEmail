/**
 * 
 */
package org.sharetomail.test.intent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.sharetomail.MainActivity;
import org.sharetomail.R;
import org.sharetomail.test.DummyEmailAppActivity;
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

public class SpecifiedAppTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TEST_SUBJECT = "test subject";
	private static final String TEST_LINK = "http://test.lnk";
	private static final String EMAIL_APP_NAME = DummyEmailAppActivity.class
			.getName();
	private static final String EMAIL_APP_PKG_NAME = DummyEmailAppActivity.class
			.getPackage().getName();

	private SharedPreferences sharedPreferences;

	private Solo solo;

	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\""
			+ EMAIL_APP_PKG_NAME + "\",\"EMAIL_APP_NAME\":\"" + EMAIL_APP_NAME
			+ "\",\"EMAIL_ADDRESS\":\"" + defaultEmail + "\"}";
	private File resultFile;

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

		resultFile = DummyEmailAppActivity.getResultFile();

		if (resultFile.exists()) {
			resultFile.delete();
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

	@Override
	public MainActivity getActivity() {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_TEXT, TEST_LINK);
		intent.putExtra(Intent.EXTRA_SUBJECT, TEST_SUBJECT);
		setActivityIntent(intent);
		return super.getActivity();
	}

	public void testUseSpecifiedApp() throws InterruptedException,
			FileNotFoundException, IOException {
		String subjectPrefix = solo.getCurrentActivity().getString(
				R.string.default_email_subject_prefix);
		solo.clickOnView(Util.getEmailAddressesListView(solo).getChildAt(0));

		Thread.sleep(1000);

		assertTrue(resultFile.exists());
		assertTrue(resultFile.isFile());
		assertTrue(resultFile.canRead());

		Properties resultProps = new Properties();
		resultProps.load(new FileInputStream(resultFile));

		assertEquals(Arrays.toString(new String[] { defaultEmail }),
				resultProps.getProperty(Intent.EXTRA_EMAIL));
		assertEquals(TEST_LINK, resultProps.get(Intent.EXTRA_TEXT));
		assertEquals(subjectPrefix + TEST_SUBJECT,
				resultProps.get(Intent.EXTRA_SUBJECT));
	}
}
