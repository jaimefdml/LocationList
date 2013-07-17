package com.jaimefdml.locationlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ItemDetailsActivity extends FragmentActivity {
TextView tvItemName;
TextView tvItemDescription;
Double itemLatitude;
Double itemLongitude;
LatLng latlng;
GoogleMap map;
Marker marker;
MarkerOptions mrkopt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_details);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		tvItemName=(TextView)findViewById(R.id.tvnameitemdetails);
		tvItemDescription=(TextView)findViewById(R.id.tvdescriptionitemdetails);
		itemLatitude = intent.getDoubleExtra("itemLatitude", 0.0);
		itemLongitude = intent.getDoubleExtra("itemLongitude", 0.0);
		latlng = new LatLng(itemLongitude,itemLatitude);
		mrkopt = new MarkerOptions().position(latlng).draggable(false);
		tvItemName.setText(intent.getStringExtra("itemName"));
		tvItemDescription.setText(intent.getStringExtra("itemDescription"));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_item_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapitems)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.addMarker(mrkopt);
		CameraUpdate camUp = CameraUpdateFactory.newLatLngZoom(latlng, 12);
		map.animateCamera(camUp);
		
	}

}
