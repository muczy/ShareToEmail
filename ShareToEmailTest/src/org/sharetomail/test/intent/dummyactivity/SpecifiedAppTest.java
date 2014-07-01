/**
 * 
 */
package org.sharetomail.test.intent.dummyactivity;

import java.util.Arrays;
import java.util.Properties;

import org.sharetomail.MainActivity;

import android.content.Intent;

public class SpecifiedAppTest extends DummyActivityTestBase {

	private static final String TEST_SUBJECT = "test subject";
	private static final String TEST_LINK = "http://test.lnk";

	private String defaultEmail = "default.text@mail.org";
	private String defaultEmailConfigLine = "{\"EMAIL_APP_PACKAGE_NAME\":\""
			+ EMAIL_APP_PKG_NAME + "\",\"EMAIL_APP_NAME\":\"" + EMAIL_APP_NAME
			+ "\",\"EMAIL_ADDRESS\":\"" + defaultEmail + "\"}";

	@Override
	public MainActivity getActivity() {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_TEXT, TEST_LINK);
		intent.putExtra(Intent.EXTRA_SUBJECT, TEST_SUBJECT);
		setActivityIntent(intent);
		return super.getActivity();
	}

	@Override
	protected String getDefaultEmailConfigLine() {
		return defaultEmailConfigLine;
	}

	@Override
	protected void doAsserts(Properties resultProps) {
		assertEquals(Arrays.toString(new String[] { defaultEmail }),
				resultProps.getProperty(Intent.EXTRA_EMAIL));
		assertEquals(TEST_LINK, resultProps.get(Intent.EXTRA_TEXT));
		assertEquals(getSubjectPrefix() + TEST_SUBJECT,
				resultProps.get(Intent.EXTRA_SUBJECT));
	}
}
