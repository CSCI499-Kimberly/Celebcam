package com.celebcam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.EditText;

public class TwitterPrefActivity extends PreferenceActivity {

	final String TAG = "TwitterPrefActivity";
	
	EditTextPreference mPin;
	
	EditText mEditText;
	
	public void onCreate( Bundle bundle )
	{
		super.onCreate( bundle );
		
		addPreferencesFromResource( R.xml.twitter_prefs );
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("request_token")));

		startActivity(intent);
		
		EditTextPreference pin = (EditTextPreference) findPreference("twitter_pin");
		
//		pin.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//
//			@Override
//			public boolean onPreferenceClick(Preference arg0) {
//				Log.d(TAG, "Text Pin Clicked Before Test");
//				if( ((EditTextPreference)arg0).getEditText().toString().length() > 0 )
//				{
//					Log.d(TAG, "Text Pin Clicked.");
//					setResult( RESULT_OK );
//					finish();
//				}
//				return true;
//			}
//		
//		});
		
		mEditText = pin.getEditText();
		
	}
	

	
	public void onResume()
	{
		super.onResume();
		
		Log.d(TAG, "onResume called.");
		
	}
	
	public void onPause()
	{
		super.onPause();
		if( mEditText.getText().length() > 0 )
		{
			Log.d(TAG, "Setting results and finishing...");
			setResult( RESULT_OK );
			finish();
		}
	}
}
