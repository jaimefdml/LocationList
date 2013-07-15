package com.jaimefdml.locationlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;

public class DialogGPS extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Builds the dialog using the builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.title_dialog_GPS);
		builder.setMessage(R.string.message_dialog_GPS);
		builder.setPositiveButton(R.string.buttonOk_dialog_gps,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Opens Settings to activate GPS.
						Intent settingsIntent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(settingsIntent);

					}

				});

		Dialog dialog = builder.create();
		return dialog;

	}
}
