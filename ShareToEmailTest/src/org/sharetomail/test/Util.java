package org.sharetomail.test;

import android.view.View;
import android.widget.ListView;

import com.robotium.solo.Solo;

public class Util {
	public static ListView getEmailAddressesListView(Solo solo) {
		solo.waitForView(org.sharetomail.R.id.emailAddressesListView, 1, 2000);
		return (ListView) Util.findViewById(solo,
				org.sharetomail.R.id.emailAddressesListView);
	}

	public static View findViewById(Solo solo, int id) {
		return solo.getCurrentActivity().findViewById(id);
	}
}
