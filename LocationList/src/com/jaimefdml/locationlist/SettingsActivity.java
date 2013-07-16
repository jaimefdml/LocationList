package com.jaimefdml.locationlist;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	public final static String KEY_NOTIFICATION_RADIO="NotifRadioKey";
	public final static String KEY_BASKET_RADIO="BasketRadioKey";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}
}
