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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaimefdml.locationlist.TodoListContentProvider.BaseTodoList;

public class MapActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
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
		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setMyLocationEnabled(true);
		
		//Shows all the places the list already has marked.
		String [] projection = {BaseTodoList.KEY_NAME,
				BaseTodoList.KEY_LATITUDE,
				BaseTodoList.KEY_LONGITUDE,
				BaseTodoList.KEY_BASKET
				
		};
		ContentResolver cr= getContentResolver();
		Cursor cursor = cr.query(TodoListContentProvider.CONTENT_URI, projection, null, null, BaseTodoList.KEY_BASKET);
		//Maybe depending on the basket the places belong to, we could set different
		//colors in the icons.
		while(cursor.moveToNext()){
			LatLng latlng = new LatLng(cursor.getDouble(1),cursor.getDouble(2));
			String name = cursor.getString(0);
			map.addMarker(new MarkerOptions().position(latlng).title(name));
		}
		
		//Tries to center the map in the last known position
		LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		Location lastLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(lastLoc==null)
			lastLoc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		if(lastLoc!=null){
		LatLng latlong = new LatLng(lastLoc.getLatitude(),lastLoc.getLongitude());
		CameraUpdate camUp = CameraUpdateFactory.newLatLngZoom(latlong, 12);
		
		map.animateCamera(camUp);
		}
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				Intent intent = new Intent();
				intent.putExtra("Latitude", arg0.latitude);
				intent.putExtra("Longitude", arg0.longitude);
				setResult(RESULT_OK, intent);
				Toast.makeText(getApplicationContext(), "Set as the location",
						Toast.LENGTH_LONG).show();

			}

		});
	}

}
