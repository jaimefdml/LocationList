package com.jaimefdml.locationlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class DialogWifi extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.title_dialog_WiFi);
		builder.setMessage(R.string.message_dialog_WiFi);
		builder.setPositiveButton(R.string.buttonOk_dialog_WiFi, 
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent settingsWifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
						startActivity(settingsWifiIntent);
						
					}
			
		});
		
		Dialog dialogwifi = builder.create();
		return dialogwifi;
		
	}

}
