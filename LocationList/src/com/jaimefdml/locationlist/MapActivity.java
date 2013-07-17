package com.jaimefdml.locationlist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaimefdml.locationlist.TodoListContentProvider.BaseTodoList;

public class MapActivity extends FragmentActivity {
	SearchView svMap;
	Button buttonSave;
	GoogleMap map;
	MarkerOptions newmarkopt;
	Marker newmarker;
	
	Double newLatitude = 0.0;
	Double newLongitude = 0.0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		//Still have to implement the search.
		this.svMap = (SearchView) findViewById(R.id.svMap);
		this.buttonSave = (Button) findViewById(R.id.buttonSave);
		this.newmarkopt = new MarkerOptions()
				.title("New Location")
				.snippet("Drag and drop to set the location of the new item")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
				.draggable(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		setResult(RESULT_CANCELED);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * Gets the reference to the map and sets its features: hybrid, able to
		 * locate and adds a blue marker to indicate the position of the new
		 * location
		 */
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setMyLocationEnabled(true);
		

		/*
		 * Shows all the places the list already has marked. Querys the DB, and
		 * places all the results using red markers in the map.
		 */
		String[] projection = { BaseTodoList.KEY_NAME,
				BaseTodoList.KEY_LATITUDE, BaseTodoList.KEY_LONGITUDE,
				BaseTodoList.KEY_BASKET

		};
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(TodoListContentProvider.CONTENT_URI,
				projection, null, null, BaseTodoList.KEY_BASKET);
		// Maybe depending on the basket the places belong to, we could set
		// different
		// colors in the icons.
		while (cursor.moveToNext()) {
			LatLng latlng = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
			String name = cursor.getString(0);
			map.addMarker(new MarkerOptions().position(latlng).title(name)
					.draggable(false));
			
		}

		/*
		 * Centers the map view in the last known position, and sets the new
		 * location blue marker in last known position.
		 */
		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location lastLoc = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastLoc == null)
			lastLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (lastLoc != null) {
			LatLng latlong = new LatLng(lastLoc.getLatitude(),
					lastLoc.getLongitude());
			CameraUpdate camUp = CameraUpdateFactory.newLatLngZoom(latlong, 12);

			map.animateCamera(camUp);
			// Sets a Marker for the new location in the last known location
			// position.
			newmarkopt.position(latlong);
			newmarker = map.addMarker(newmarkopt);
			newLatitude = latlong.latitude;
			newLongitude = latlong.longitude;
		}

		/*
		 * When map is long clicked, the blue marker that indicates the new
		 * location is set in the clicked location.
		 */
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				newmarkopt.position(arg0);
				newmarker.remove();
				newmarker = map.addMarker(newmarkopt);
				

			}

		});
		

		/*
		 * Sets the location of the new place when the blue new location
		 * marker is dropped.
		 */
		map.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDrag(Marker arg0) {
				// No action

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				// Sets as location the End location of the marker.
				if (marker.getTitle() == "New Location") {
					newLatitude = marker.getPosition().latitude;
					newLongitude = marker.getPosition().longitude;
					//Shall be commented
					Toast.makeText(getApplicationContext(),
							"Set as the location", Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onMarkerDragStart(Marker arg0) {
				// No action

			}

		});
	}

	/*When the user already has chosen the location using the blue marker,
	 * it sets the intent extras to return to previous activity, set result to OK,
	 * and finishes the activity itself.
	 */
	public void OnClickButtonSave(View view) {
		
		Intent intent = new Intent();
		intent.putExtra("Latitude", newLatitude);
		intent.putExtra("Longitude", newLongitude);
		setResult(RESULT_OK, intent);
		finish();
	}
}
