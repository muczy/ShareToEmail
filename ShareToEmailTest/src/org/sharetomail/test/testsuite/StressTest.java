package org.sharetomail.test.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.sharetomail.test.CompatilbityTest;
import org.sharetomail.test.MainActivityIntentTest;
import org.sharetomail.test.MainActivityTest;

public class StressTest {

	public static final int RUNS = 2;

	public static Test suite() {
		TestSuite suite = new TestSuite();

		for (int i = 0; i < RUNS; i++) {
			suite.addTestSuite(MainActivityTest.class);
			suite.addTestSuite(CompatilbityTest.class);
			suite.addTestSuite(MainActivityIntentTest.class);
		}

		return suite;
	}
}
