package com.celebcam;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PhotoBrowserActivity extends Activity{
	
	SQLiteDatabase   mDatabase;
	CelebCamDbHelper mDbHelper;
	
	public void onCreate( Bundle bundle )
	{
		super.onCreate( bundle );
		setContentView( R.layout.photo_browser );
		
		mDatabase = mDbHelper.getReadableDatabase();
		
        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new CelebCamImageAdapter(this));

        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(PhotoBrowserActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        
	}
}
