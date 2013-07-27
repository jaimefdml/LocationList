package com.jaimefdml.locationlist;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.jaimefdml.locationlist.TodoListContentProvider.BaseTodoList;

public class LocationService extends IntentService {

	public LocationService() {
		super("LocationService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// Gets the last known location.
		Location lastLoca = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// Checks the first time every element in the list. In case the user
		// didn't move.
		if (lastLoca == null)
			lastLoca = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastLoca != null)
			checkAllLocations(lastLoca);

		// Location listener.
		LocationListener loclis = new LocationListener() {
			@Override
			public void onLocationChanged(Location currentLocation) {
				checkAllLocations(currentLocation);
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}

		};
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loclis);

	}

	// Methods that checks all the locations in the db with the current
	// locations.
	// If there is any match, its throws a notification.
	public void checkAllLocations(Location currentLocation) {
		// Check that all the locations in the list.
		// If anyone of the locations is nearer than X km, notify the
		// user.
		Double currentLatitude = currentLocation.getLatitude();
		Double currentLongitude = currentLocation.getLongitude();
		ContentResolver cr = getContentResolver();
		String[] projection = { BaseTodoList._ID, BaseTodoList.KEY_NAME,
				BaseTodoList.KEY_LATITUDE, BaseTodoList.KEY_LONGITUDE,
				BaseTodoList.KEY_BASKET };
		Cursor cursor = cr.query(TodoListContentProvider.CONTENT_URI,
				projection, null, null, BaseTodoList.KEY_BASKET);
		SharedPreferences sharedpref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String nr = sharedpref.getString(
				SettingsActivity.KEY_NOTIFICATION_RADIO, "2");
		Integer notifiradio = Integer.parseInt(nr) * 1000;

		// Hay un problema de redundancia de notificaciones.
		// Mirar en Teambox para la explicación.

		while (cursor.moveToNext()) {
			Double cursorLat = cursor.getDouble(2);
			Double cursorLon = cursor.getDouble(3);

			float[] results = new float[3];
			// Gets the distance between the points
			Location.distanceBetween(currentLatitude, currentLongitude,
					cursorLat, cursorLon, results);
			Float distance = results[0];
			// If you are nearer than specified distance, throw the notification
			if (distance < notifiradio) {
				// Throw status bar notifications
				// Showing has to buy KEY_NAME.
				// For the moment, just a Toast.
				Cursor sameBasketCursor = cr.query(
						TodoListContentProvider.CONTENT_URI,
						new String[] { BaseTodoList.KEY_NAME },
						BaseTodoList.KEY_BASKET + "=?",
						new String[] { Integer.toString(cursor.getInt(4)) },
						null);

				String elements = "";
				while (sameBasketCursor.moveToNext()) {
					// Here just put together all the items from the same
					// basket.
					if (sameBasketCursor.isFirst()) {
						elements = sameBasketCursor.getString(0);
					} else {
						elements.concat("\n" + sameBasketCursor.getString(0));
					}
				}
				sameBasketCursor.close();
				

				// Notification about all the items in the same Basket case.
				Resources res = getResources();
				String title = res.getString(R.string.Remember);
				NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
						getApplicationContext()).setContentTitle(title)
						.setContentText(elements)
						.setSmallIcon(R.drawable.ic_launcher);
				Intent resultIntent = new Intent(getApplicationContext(),
						MyListActivity.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder
						.create(getApplicationContext());
				stackBuilder.addParentStack(MyListActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent pendInt = stackBuilder.getPendingIntent(0,
						PendingIntent.FLAG_CANCEL_CURRENT);
				notBuilder.setContentIntent(pendInt);
				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(0, notBuilder.build());
				sameBasketCursor.close();
			}

		}
		cursor.close();
	}
}
