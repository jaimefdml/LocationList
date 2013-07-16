package com.jaimefdml.locationlist;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Load the preferences from an xml file.
		addPreferencesFromResource(R.xml.preferences);
		
	}

}
