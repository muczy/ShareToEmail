package org.sharetomail.util;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DefaultItemHandlingAdapter<T> extends BaseAdapter {

	private Context context;
	private List<T> objects;
	private T defaultItem;

	public DefaultItemHandlingAdapter(Context context, List<T> objects,
			T defaultItem) {
		this.context = context;
		this.objects = objects;
		this.defaultItem = defaultItem;
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public T getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);

		}

		TextView defaultItemHandlingTextView = (TextView) rowView
				.findViewById(android.R.id.text1);

		// Make the default item bold.
		if (objects.get(position).equals(defaultItem)) {
			SpannableString spannableString = new SpannableString(
					String.valueOf(objects.get(position)));
			spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spannableString.length(), 0);
			defaultItemHandlingTextView.setText(spannableString,
					TextView.BufferType.SPANNABLE);
		} else {
			defaultItemHandlingTextView.setText(String.valueOf(objects
					.get(position)));
		}

		return rowView;
	}

	/**
	 * Sets the default item.
	 * 
	 * @param defaultItem
	 *            the new default item
	 */
	public void setDefaultItem(T defaultItem) {
		this.defaultItem = defaultItem;
		notifyDataSetChanged();
	}

	public void clear() {
		objects.clear();
		notifyDataSetChanged();
	}

	public void addAll(List<T> objectList) {
		objects.addAll(objectList);
		notifyDataSetChanged();
	}

}
