/*******************************************************************************
 * Copyright 2013 Peter Mihaly Avramucz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.sharetomail.util;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DefaultItemHandlingArrayAdapter extends ArrayAdapter<String> {

	private Context context;
	private List<String> objects;
	private String defaultItem;

	public DefaultItemHandlingArrayAdapter(Context context,
			List<String> objects, String defaultItem) {
		super(context, android.R.layout.simple_list_item_1, objects);
		this.context = context;
		this.objects = objects;
		this.defaultItem = defaultItem;
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
		if (defaultItem.equals(objects.get(position))) {
			SpannableString spannableString = new SpannableString(
					objects.get(position));
			spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spannableString.length(), 0);
			defaultItemHandlingTextView.setText(spannableString,
					TextView.BufferType.SPANNABLE);
		} else {
			defaultItemHandlingTextView.setText(objects.get(position));
		}

		return rowView;
	}

	/**
	 * Sets the default item.
	 * 
	 * @param defaultItem
	 *            the new default item
	 */
	public void setDefaultItem(String defaultItem) {
		this.defaultItem = defaultItem;
		notifyDataSetChanged();
	}

}
