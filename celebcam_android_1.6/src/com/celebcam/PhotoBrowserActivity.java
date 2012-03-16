package com.celebcam;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PhotoBrowserActivity extends Activity{
	
	SQLiteDatabase   mDatabase;
	CelebCamDbHelper mDbHelper;
	
	static String[]  mFileNames;	
	static Bitmap[]  mBitmaps;
	
	static int i = 0;
	static int mSize;
	
	static public Bitmap getNextBitmap()
	{
		
		if( i < mSize-1 )
		{
			i++;
		}
		else 
		{ 
			i = 0;
		}
		
		return mBitmaps[i];
	}
	
	Gallery gallery;
	public void onCreate( Bundle bundle )
	{
		super.onCreate( bundle );
		setContentView( R.layout.photo_browser );
		Log.d("PhotoBrowser","onCreate");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        gallery = (Gallery) findViewById(R.id.gallery);
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(PhotoBrowserActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
		mDbHelper = new CelebCamDbHelper(this);
        
	}
	
	public void onResume()
	{
		super.onResume();
		
		Log.d("PhotoBrowser","onResume");
		update();
	}
	
	public void onPause()
	{
		super.onPause();
		
	}
	
	public void update()
	{
		mDatabase = mDbHelper.getReadableDatabase();
        
		try {
			Cursor cursor = mDatabase.query(CelebCamDbHelper.TABLE,new String[]{CelebCamDbHelper.C_SOURCE}, null, null, null, null, null);
			
			mFileNames = new String[ cursor.getCount()];
			mBitmaps   = new Bitmap[ cursor.getCount()];
			mSize	   = cursor.getCount();
				try {

					int i = 0;
					
					Log.d("PhotoBrowser", "");
					Log.d("PhotoBrowser", "**********************************");
					while( cursor.moveToNext() )
					{
						mFileNames[i] = cursor.getString(0);
						
						
						if( mFileNames[i] != null )
						{
							Log.d("PhotoBrowser", mFileNames[i]);
						
							mBitmaps[i] = BitmapFactory.decodeFile(mFileNames[i]);
						}
						
						Log.d("PhotoBrowser", "");
						Log.d("PhotoBrowser", "");
						i++;
					}
					
		        } 
				finally 
				{
					cursor.close();
		        }
	    } 
		finally 
		{
	        mDatabase.close();
	    }
		
		gallery.setAdapter(new CelebCamImageAdapter(this));
	}
	
	public void delete( int index )
	{
		
	}
	
	public void sendViaEmail( int index )
	{
		
	}
	
	public void postToTwitter( int index )
	{
		
	}
	
	public void postToFacebook( int index )
	{
		
	}
}
