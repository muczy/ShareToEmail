package org.sharetomail;

import org.sharetomail.util.Constants;
import org.sharetomail.util.EmailAppListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EmailAppSelectorActivity extends ActionBarActivity implements
		OnItemClickListener {

	private EmailAppListAdapter emailAppListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_app_selector);

		emailAppListAdapter = new EmailAppListAdapter(this, getPackageManager());

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_email_app_selector, container, false);

			ListView emailAppListView = (ListView) rootView
					.findViewById(R.id.emailAppListView);
			emailAppListView
					.setAdapter(((EmailAppSelectorActivity) getActivity())
							.getEmailAppListAdapter());
			emailAppListView
					.setOnItemClickListener((EmailAppSelectorActivity) getActivity());

			return rootView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent data = new Intent();

		if (emailAppListAdapter.getItem(position).activityInfo == null) {
			data.putExtra(Constants.EMAIL_APP_NAME_INTENT_KEY, "");
		} else {
			data.putExtra(Constants.EMAIL_APP_NAME_INTENT_KEY,
					emailAppListAdapter.getItem(position).activityInfo.name);
		}

		if (emailAppListAdapter.getItem(position).activityInfo == null) {
			data.putExtra(Constants.EMAIL_APP_PACKAGE_NAME_INTENT_KEY, "");
		} else {
			data.putExtra(
					Constants.EMAIL_APP_PACKAGE_NAME_INTENT_KEY,
					emailAppListAdapter.getItem(position).activityInfo.packageName);
		}

		setResult(Constants.EMAIL_APP_SELECTOR_ACTIVITY_REQUEST_CODE, data);
		finish();
	}

	public ListAdapter getEmailAppListAdapter() {
		return emailAppListAdapter;
	}
}
