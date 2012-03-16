package com.celebcam;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.content.Context;
import android.util.Log;


public class CelebCamDbHelper extends SQLiteOpenHelper implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (1*4);
	}


	public int getStaticBytes() {

		return 9;
	}

	static final String TAG     = "CelebCamDBHelper";
	static final String DB_NAME = "image_gallery.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "image_gallery";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE    = "source";
	static final String C_TEXT  = "txt";
	static final String C_USER  = "user";
	
	Context context;

	public CelebCamDbHelper( Context context ) {
		super( context, DB_NAME, null, DB_VERSION);
		this.context = context;
		
		CCDebug.registerMemoryWatcher( this );
	}

	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, "
		+ C_CREATED_AT + " int, " + C_SOURCE + " text, " + C_USER + " text, " + C_TEXT + " text)";

		db.execSQL( sql );

		Log.d("DataAcquisitionActivity", "onCreated sql: " + sql );
	}

	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("drop table if exists " + TABLE);

		Log.d( TAG, "onUpdated");

		onCreate(db);
	}
}
