package com.jaimefdml.locationlist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class TodoListContentProvider extends ContentProvider {

	// URI
	public static final String uri = "content://com.jaimefdml.locationlist/ListLocationTable";
	public static final Uri CONTENT_URI = Uri.parse(uri);

	// Clase interna para declarar las constantes de columnas
	public static final class BaseTodoList implements BaseColumns{
		private BaseTodoList() {
		}
		
		public static final String KEY_NAME = "Name";
		public static final String KEY_DESCRIPTION = "Description";
		public static final String KEY_LATITUDE = "Latitude";
		public static final String KEY_LONGITUDE = "Altitude";
		public static final String KEY_KM="Km";
		public static final String KEY_BASKET="Basket";

	}

	// UriMatcher
	private static final int ListLocationTable = 1;
	private static final int ElementLocation = 2;
	private static final UriMatcher uriMatcher;
	// Inicializamos Urimatcher
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.jaimefdml.locationlist", "ListLocationTable",
				ListLocationTable);
		uriMatcher.addURI("com.jaimefdml.locationlist", "ListLocationTable/#",
				ElementLocation);

	}

	//Referencia al DBHelper
	private ListLocationOpenHelper mOpenHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int cont;
		String where = selection;
		if (uriMatcher.match(uri) == ElementLocation) {
			//No tiene que ser + selection, tiene que ser + la ultima parte de la URI
			where = "_ID=" + uri.getLastPathSegment();
		}
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		cont = db.delete(ListLocationOpenHelper.TABLE_NAME, where,
				selectionArgs);
		return cont;
	}

	@Override
	public String getType(Uri uri) {

		String ListLocation = "vnd.android.cursor.dir/vnd.jaimefdml.listlocation";
		String LocationElement = "vnd.android.cursor.item/vnd.jaimefdml.listlocation";

		int match = uriMatcher.match(uri);
		switch (match) {
		case ListLocationTable:
			return ListLocation;
		case ElementLocation:
			return LocationElement;
		default:
			return null;

		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		long regID = 1;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		regID = db.insert(ListLocationOpenHelper.TABLE_NAME, null, values);
		Uri newUri = ContentUris.withAppendedId(uri, regID);
		return newUri;

	}

	@Override
	public boolean onCreate() {
		// Just creates the OpenHelper of the DB.
		mOpenHelper = new ListLocationOpenHelper(getContext(),
				ListLocationOpenHelper.DATABASE_NAME, null,
				ListLocationOpenHelper.DATABASE_VERSION);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		String where = selection;
		if (uriMatcher.match(uri) == ElementLocation) {
			where = ("_ID=" + uri.getLastPathSegment());
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query(ListLocationOpenHelper.TABLE_NAME, projection,
				where, selectionArgs, null, null, sortOrder);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int cont;

		String where = selection;
		if (uriMatcher.match(uri) == ElementLocation) {
			where = "_ID=" + selection;
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		cont = db.update(ListLocationOpenHelper.TABLE_NAME, values, where,
				selectionArgs);

		return cont;

	}
}
