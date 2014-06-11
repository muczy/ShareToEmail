package org.sharetomail.util;

import java.util.LinkedList;
import java.util.List;

import org.sharetomail.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmailAppListAdapter extends BaseAdapter {

	private static final String TAG = EmailAppListAdapter.class.getName();

	private Context context;

	private List<ResolveInfo> emailApps = new LinkedList<ResolveInfo>();
	private PackageManager packageManager;

	public EmailAppListAdapter(Context context, PackageManager packageManager) {
		this.context = context;
		this.packageManager = packageManager;

		// "Application selector" empty app.
		emailApps.add(new ResolveInfo());

		Intent sendMailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				Constants.MAILTO_SCHEME, "", null));
		emailApps.addAll(packageManager.queryIntentActivities(sendMailIntent,
				PackageManager.MATCH_DEFAULT_ONLY));

		if (emailApps == null) {
			emailApps = new LinkedList<ResolveInfo>();
		}
	}

	public void add(ResolveInfo resolveInfo) {
		emailApps.add(resolveInfo);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return emailApps.size();
	}

	@Override
	public ResolveInfo getItem(int position) {
		return emailApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.email_app_list_item, parent, false);

		ImageView emailAppImageView = (ImageView) rowView
				.findViewById(R.id.emailAppImageView);
		TextView emailAppTitleTextView = (TextView) rowView
				.findViewById(R.id.emailAppTitleTextView);

		try {
			emailAppImageView.setImageDrawable(emailApps.get(position)
					.loadIcon(packageManager));
		} catch (IllegalStateException e) {
			Log.e(TAG,
					"IllegalStateException while loading icon for email app!",
					e);
		} catch (NullPointerException e) {
			// Possible LG P880 - Android 4.0.3 related bug from user Darkcomet.
			Log.e(TAG,
					"NullPointerException while loading icon for email app!", e);
		}

		CharSequence appLabel;
		try {
			appLabel = emailApps.get(position).loadLabel(packageManager);
		} catch (IllegalStateException e) {
			appLabel = context.getString(R.string.app_selector);
		}

		emailAppTitleTextView.setText(appLabel);

		return rowView;
	}
}
