package org.sharetomail.test.testsuite;

import org.sharetomail.test.CompatilbityTest;
import org.sharetomail.test.MainActivityTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class StressTest {

	public static final int RUNS = 2;

	public static Test suite() {
		TestSuite suite = new TestSuite();

		for (int i = 0; i < RUNS; i++) {
			suite.addTestSuite(MainActivityTest.class);
			suite.addTestSuite(CompatilbityTest.class);
		}

		return suite;
	}
}
