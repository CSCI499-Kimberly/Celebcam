package com.celebcam;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class SettingsPrefActivity extends PreferenceActivity {

	public void onCreate( Bundle bundle )
	{
		super.onCreate( bundle );
		
		addPreferencesFromResource( R.xml.settings );
	}
}
