package com.jaimefdml.locationlist;

import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.jaimefdml.locationlist.TodoListContentProvider.BaseTodoList;

public class MyListActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	int requestCodeOnAddElement = 0;
	final static int LIST_ID = 0;
	// Cursor adapter to fill in
	SimpleCursorAdapter mAdapter;

	// Columns to query
	private static final String[] projection = { BaseTodoList._ID,
			BaseTodoList.KEY_NAME, BaseTodoList.KEY_DESCRIPTION };

	Button buttonAddElement;
	Switch switchService;
	ListView lvListToDo;

	/*
	 * Performs the initialization of all the UI widgets: - The Button to Add an
	 * Element to the list, - The switch to activate or deactivate the service,
	 * - The List of things to do. (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_list);

		this.buttonAddElement = (Button) findViewById(R.id.buttonAddElement);
		this.switchService = (Switch) findViewById(R.id.switchService);
		this.lvListToDo = (ListView) findViewById(R.id.listToDo);
		fillData();
	}

	/*
	 * Performs all the actions when the user has the activity control: - Fetchs
	 * all the information from the content provider. - Shows the information in
	 * the List.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		this.lvListToDo
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// How to delete?
						ContentResolver cr = getContentResolver();

						String uri = TodoListContentProvider.uri + "/"
								+ Long.toString(id);
						Uri locator = Uri.parse(uri);
						cr.delete(locator, null, null);

						// Cómo se refresca el listView??
						reFillData();

						return true;
					}

				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_list, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == requestCodeOnAddElement) {
			if (resultCode == RESULT_OK) {
				reFillData();

			}
		}
	}

	/*
	 * Performs the actions of the Button Add Element: - Launch
	 * AddElementActivity.class
	 */
	public void onAddElementClick(View view) {

		Intent intent = new Intent(this, AddElementActivity.class);
		startActivityForResult(intent, requestCodeOnAddElement);
	}

	/*
	 * Performs the actions of the Switch On/Off: - If on, deactivates the
	 * service. - If off, activates the service.
	 */
	public void onSwitchServiceClick(View view) {
		if (switchService.isChecked()) {
			// When it is activated: throw the service
			//Toast.makeText(this, "Activated", Toast.LENGTH_LONG).show();
			LocationManager lm = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			boolean gps_on = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean wifi_on = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if(!gps_on){
				//Activate GPS
				//Should throw a Dialog, and open the settings activity.
				//For the moment, just a Toast
				Toast.makeText(this, "Please, activate your GPS", Toast.LENGTH_LONG).show();
				this.switchService.setChecked(false);
			}
			else if (!wifi_on){
				//Activate WIFI
				//Should throw a Dialog, and open the settings activity.
				//For the moment, just a Toast
				Toast.makeText(this, "Please, activate your WiFi", Toast.LENGTH_LONG).show();
				this.switchService.setChecked(false);
			}else{
				//Throw service with all the providers already activated
				Intent intent = new Intent(this,LocationService.class);
				startService(intent);
			}
		}
		if (!switchService.isChecked()) {
			// When it is deactivated: finish the service.
			//Toast.makeText(this, "Not Activated", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		Uri uri = TodoListContentProvider.CONTENT_URI;

		return new CursorLoader(getApplicationContext(), uri, projection, null,
				null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		mAdapter.swapCursor(data);
		// Debería de actualizar mi user interface (listView) aquí

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	private void fillData() {
		// Initiales the Loader
		String[] from = projection;
		int[] to = { android.R.id.text1, android.R.id.text1, android.R.id.text2 };
		getLoaderManager().initLoader(LIST_ID, null, this);
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, from, to, 0);
		lvListToDo.setAdapter(mAdapter);

	}

	private void reFillData() {
		getLoaderManager().restartLoader(LIST_ID, null, this);
	}

}
