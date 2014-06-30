package org.sharetomail.test.intent.dummyactivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.sharetomail.MainActivity;
import org.sharetomail.R;
import org.sharetomail.test.DummyEmailAppActivity;
import org.sharetomail.test.Util;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Window;
import android.view.WindowManager;

import com.robotium.solo.Solo;

public abstract class DummyActivityTestBase extends
		ActivityInstrumentationTestCase2<MainActivity> {

	protected static final String EMAIL_APP_NAME = DummyEmailAppActivity.class
			.getName();
	protected static final String EMAIL_APP_PKG_NAME = DummyEmailAppActivity.class
			.getPackage().getName();

	private SharedPreferences sharedPreferences;

	private Solo solo;

	private File resultFile;

	public DummyActivityTestBase() {
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

	@Override
	public MainActivity getActivity() {
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
				getDefaultEmailConfigLine());

		editor.commit();
	}

	protected String getSubjectPrefix() {
		return solo.getCurrentActivity().getString(
				R.string.default_email_subject_prefix);
	}

	protected abstract String getDefaultEmailConfigLine();

	public void testDummyActivityTest() throws InterruptedException,
			FileNotFoundException, IOException {
		solo.clickOnView(Util.getEmailAddressesListView(solo).getChildAt(0));

		Thread.sleep(1000);

		assertTrue(resultFile.exists());
		assertTrue(resultFile.isFile());
		assertTrue(resultFile.canRead());

		Properties resultProps = new Properties();
		resultProps.load(new FileInputStream(resultFile));

		doAsserts(resultProps);
	}

	protected abstract void doAsserts(Properties resultProps);

}
