package org.sharetomail.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class DummyEmailAppActivity extends Activity {

	private static final String TAG = DummyEmailAppActivity.class.getName();

	public static final String t = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Properties props = new Properties();

		props.put(
				Intent.EXTRA_EMAIL,
				Arrays.toString(getIntent().getStringArrayExtra(
						Intent.EXTRA_EMAIL)));
		props.put(Intent.EXTRA_TEXT,
				getIntent().getStringExtra(Intent.EXTRA_TEXT));
		props.put(Intent.EXTRA_SUBJECT,
				getIntent().getStringExtra(Intent.EXTRA_SUBJECT));

		try {
			props.store(new FileOutputStream(getResultFile()), null);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Result file could not be found", e);
		} catch (IOException e) {
			Log.e(TAG, "I/O error while writing to result file "
					+ getResultFile().getAbsolutePath(), e);
		}

		finish();
	}

	public static File getResultFile() {
		return new File(Environment.getExternalStorageDirectory(),
				DummyEmailAppActivity.class.getName() + ".props");
	}
}
