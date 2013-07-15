package com.jaimefdml.locationlist;

import com.jaimefdml.locationlist.TodoListContentProvider.BaseTodoList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.location.*;

public class AddElementActivity extends Activity {
	EditText etNewName;
	EditText etNewDescription;
	Button buttonLocate;
	NumberPicker kmPicker;
	Button buttonSave;
	Double latitude = 0.0;
	Double longitude = 0.0;
	int result = RESULT_CANCELED;
	int rcMapActivity = 0;
	int numKm = 1;
	int basketNum = 0;

	/*
	 * Gets the handler of all the UI widgets: two edittexts and two buttons.
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_element);
		this.etNewName = (EditText) findViewById(R.id.etNewName);
		this.etNewDescription = (EditText) findViewById(R.id.etNewDescription);
		this.buttonLocate = (Button) findViewById(R.id.buttonLocate);
		this.buttonSave = (Button) findViewById(R.id.buttonSave);
		this.kmPicker = (NumberPicker) findViewById(R.id.numberPickerKm);
		kmPicker.setMaxValue(10);
		kmPicker.setMinValue(1);

	}

	/*
	 * Performs all the proper actions: (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_element, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == rcMapActivity) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), "Location taken",
						Toast.LENGTH_LONG).show();
				latitude = data.getDoubleExtra("Latitude", 0.0);
				longitude = data.getDoubleExtra("Longitude", 0.0);

			}
		}
	}

	public void onLocateClick(View view) {
		// Checks GPS, Wifi and so on:
		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_on = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean wifi_on = lm
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!gps_on) {
			// Activate GPS
			// Should throw a Dialog, and open the settings activity.
			DialogGPS dialog = new DialogGPS();
			dialog.show(getFragmentManager(), "GPS ACTIVATION");

		} else if (!wifi_on) {
			// Activate WIFI
			// Should throw a Dialog, and open the settings activity.
			// For the moment, just a Toast
			Toast.makeText(this, "Please, activate your WiFi",
					Toast.LENGTH_LONG).show();
		} else {
			// Launches the map activity which should allow the user
			// to pick a location.
			Intent intent = new Intent(this, MapActivity.class);
			startActivityForResult(intent, rcMapActivity);
		}

	}

	public void onSaveClick(View view) {
		ContentResolver cr = getContentResolver();
		Cursor curBaskets = cr.query(TodoListContentProvider.CONTENT_URI,
				new String[] { BaseTodoList.KEY_LATITUDE,
						BaseTodoList.KEY_LONGITUDE, BaseTodoList.KEY_BASKET },
				null, null, BaseTodoList.KEY_BASKET);
		// Puede lanzar una excepci�n si la base de datos est� vac�a??
		boolean basketIsSet = false;
		while (curBaskets.moveToNext()) {
			// Si la distancia entre la nueva compra y otra que ya se quiera
			// hacer
			// es menor de
			// X km, se a�aden a la misma cesta
			float[] results = new float[3];
			Location.distanceBetween(latitude, longitude,
					curBaskets.getDouble(0), curBaskets.getDouble(1), results);
			// Lets say a radius of 2 km
			// Should be a settings parameter
			// If the new place is near another one already pointed, both must
			// be
			// in the same basket.
			if (results[0] < 2000) {
				this.basketNum = curBaskets.getInt(2);
				// To finish looking around the cursor.
				curBaskets.moveToLast();
				basketIsSet = true;

			}
			if (curBaskets.isLast() && !basketIsSet)
				this.basketNum = curBaskets.getInt(2) + 1;

		}
		// Should insert in the content provider a row.
		// Creates the ContentValues bundle to insert in the DB.
		ContentValues values = new ContentValues();
		values.put(BaseTodoList.KEY_NAME, this.etNewName.getText().toString());
		values.put(BaseTodoList.KEY_DESCRIPTION, this.etNewDescription
				.getText().toString());
		values.put(BaseTodoList.KEY_LATITUDE, this.latitude);
		values.put(BaseTodoList.KEY_LONGITUDE, this.longitude);
		values.put(BaseTodoList.KEY_KM, this.kmPicker.getValue());
		values.put(BaseTodoList.KEY_BASKET, this.basketNum);

		Uri insertedUri = cr
				.insert(TodoListContentProvider.CONTENT_URI, values);
		if (insertedUri != null) {
			Toast.makeText(getApplicationContext(),
					"Element added successfully", Toast.LENGTH_LONG).show();
			// Resets the fields
			etNewName.setText("");
			etNewDescription.setText("");

			result = RESULT_OK;
			setResult(RESULT_OK);
		}

	}
}
