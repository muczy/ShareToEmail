package org.sharetomail;

import org.sharetomail.util.Constants;
import org.sharetomail.util.EmailAppListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EmailAppSelectorActivity extends Activity implements
		OnItemClickListener {

	private EmailAppListAdapter emailAppListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_app_selector);

		emailAppListAdapter = new EmailAppListAdapter(this, getPackageManager());

		ListView emailAppListView = (ListView) findViewById(R.id.emailAppListView);
		emailAppListView.setAdapter(emailAppListAdapter);
		emailAppListView.setOnItemClickListener(this);
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
