package org.sharetomail.test.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.sharetomail.test.CompatilbityTest;
import org.sharetomail.test.MainActivityTest;
import org.sharetomail.test.intent.EmailAppChooserTest;
import org.sharetomail.test.intent.NonExistentSpecifiedEmailApp;
import org.sharetomail.test.intent.SpecifiedAppTest;

public class StressTest {

	public static final int RUNS = 2;

	public static Test suite() {
		TestSuite suite = new TestSuite();

		for (int i = 0; i < RUNS; i++) {
			suite.addTestSuite(MainActivityTest.class);
			suite.addTestSuite(CompatilbityTest.class);
			suite.addTestSuite(EmailAppChooserTest.class);
			suite.addTestSuite(NonExistentSpecifiedEmailApp.class);
			suite.addTestSuite(SpecifiedAppTest.class);
		}

		return suite;
	}
}
