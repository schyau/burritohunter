package com.potato.burritohunter.stuff;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//practice with foreign key
public class BurritoSQLiteHalper extends SQLiteOpenHelper {
	private static final String TAG = BurritoSQLiteHalper.class.getName();
	private static final String DATABASE_NAME = "datdb.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_SELECTED_POINTS = "selected_points_table";

	public BurritoSQLiteHalper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		//database.execSQL(DATABASE_CREATE);
	}

	//TODO use the if construct that upgrades a db slowly
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		onCreate(db);
	}
}