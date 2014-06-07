/**
 * 
 */
package org.sharetomail.test;

import org.sharetomail.MainActivity;
import org.sharetomail.util.Constants;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.robotium.solo.Solo;

public class CompatilbityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private SharedPreferences sharedPreferences;

	private Solo solo;
	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailWOTLD = "default.text@mail";

	public CompatilbityTest() {
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

		addOldEmail();

		solo = new Solo(getInstrumentation(), getActivity());

		KeyguardManager myKM = (KeyguardManager) getActivity()
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (myKM.inKeyguardRestrictedInputMode()) {
			fail("Screen is locked! Please open it!");
		}
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		clearSharedPreferences();
	}

	private void clearSharedPreferences() {
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	private void addOldEmail() {
		Editor editor = sharedPreferences.edit();

		editor.putString(
				Constants.EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY,
				defaultEmail
						+ org.sharetomail.util.Constants.EMAIL_ADDRESSES_SPLIT_REGEXP
						+ defaultEmailWOTLD);

		editor.commit();
	}

	public void testConvertedEmail() {
		ListAdapter adapter = ((ListView) solo.getCurrentActivity()
				.findViewById(org.sharetomail.R.id.emailAddressesListView))
				.getAdapter();

		assertEquals(defaultEmail, String.valueOf(adapter.getItem(0)));
		assertEquals(defaultEmailWOTLD, String.valueOf(adapter.getItem(1)));
	}

}
