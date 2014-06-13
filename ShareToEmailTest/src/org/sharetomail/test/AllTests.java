package org.sharetomail.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(MainActivityTest.class);
		suite.addTestSuite(MainActivityTest.class);

		return suite;
	}
}
