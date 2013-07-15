package com.jaimefdml.locationlist;

import com.jaimefdml.locationlist.TodoListContentProvider.BaseTodoList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class ListLocationOpenHelper extends SQLiteOpenHelper {

	// Base de datos
	static final String DATABASE_NAME = "dbListLocation.db";
	static final int DATABASE_VERSION = 2;
	static final String TABLE_NAME = "ListLocationTable";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + BaseTodoList._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ BaseTodoList.KEY_NAME
			+ " TEXT," + BaseTodoList.KEY_DESCRIPTION + " TEXT,"
			+ BaseTodoList.KEY_LATITUDE + " INTEGER,"
			+ BaseTodoList.KEY_LONGITUDE + " INTEGER, "
			+ BaseTodoList.KEY_BASKET + " INTEGER);";

	public ListLocationOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);

	}

}
