package org.sharetomail.test.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.sharetomail.test.CompatibilityTest;
import org.sharetomail.test.MainActivityTest;
import org.sharetomail.test.intent.EmailAppChooserTest;
import org.sharetomail.test.intent.NonExistentSpecifiedEmailApp;
import org.sharetomail.test.intent.dummyactivity.SpecifiedAppTest;
import org.sharetomail.test.intent.dummyactivity.SpecifiedAppTestWithNewlineSubject;
import org.sharetomail.test.intent.dummyactivity.SpecifiedAppTestWithSpaceSubject;

public class StressTest {

	public static final int RUNS = 2;

	public static Test suite() {
		TestSuite suite = new TestSuite();

		for (int i = 0; i < RUNS; i++) {
			suite.addTestSuite(MainActivityTest.class);
			suite.addTestSuite(CompatibilityTest.class);
			suite.addTestSuite(EmailAppChooserTest.class);
			suite.addTestSuite(NonExistentSpecifiedEmailApp.class);
			suite.addTestSuite(SpecifiedAppTest.class);
			suite.addTestSuite(SpecifiedAppTestWithNewlineSubject.class);
			suite.addTestSuite(SpecifiedAppTestWithSpaceSubject.class);
		}

		return suite;
	}
}
