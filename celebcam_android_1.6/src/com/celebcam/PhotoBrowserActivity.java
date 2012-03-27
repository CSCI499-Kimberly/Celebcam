package com.celebcam;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
<<<<<<< HEAD
import android.content.Context;
=======
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
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
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PhotoBrowserActivity extends Activity{
	
	SQLiteDatabase   mDatabase;
	CelebCamDbHelper mDbHelper;
	
	static String[]  mFileNames;	
<<<<<<< HEAD
=======
	static Bitmap[]  mBitmaps;
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
	
	static int i = 0;
	static int mSize;
	
<<<<<<< HEAD
	private int mScreenWidth;
	private int mScreenHeight;
	
	private Cursor mCursor;
	
//	static public Bitmap getNextBitmap()
//	{
//		
//		if( i < mSize-1 )
//		{
//			i++;
//		}
//		else 
//		{ 
//			i = 0;
//		}
//		
//		return mBitmaps[i];
//	}
	
	Gallery gallery;
	SlidingDrawer mMenuDrawer;
	CelebCamButton mReturnToCamera;
	
=======
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
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
	public void onCreate( Bundle bundle )
	{
		super.onCreate( bundle );
		setContentView( R.layout.photo_browser );
		Log.d("PhotoBrowser","onCreate");
<<<<<<< HEAD

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mScreenWidth  = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		mScreenHeight = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        
		mReturnToCamera = ( CelebCamButton ) findViewById( R.id.back_to_camera_button);
		
		mReturnToCamera.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				finish();
				
			}
		});
		
		mMenuDrawer = (SlidingDrawer) findViewById( R.id.menu_drawer);
		
=======

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
        gallery = (Gallery) findViewById(R.id.gallery);
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                mMenuDrawer.animateOpen();
            }
        });
		mDbHelper = new CelebCamDbHelper(this);
<<<<<<< HEAD
=======
        
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
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
        
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
			mCursor = mDatabase.query(CelebCamDbHelper.TABLE,new String[]{CelebCamDbHelper.C_SOURCE}, null, null, null, null, null);
			
			mFileNames = new String[ mCursor.getCount()];

			mSize	   = mCursor.getCount();
			
				try {

					int i = 0;
					
					Log.d("PhotoBrowser", "");
					Log.d("PhotoBrowser", "**********************************");
					while( mCursor.moveToNext() )
					{
						mFileNames[i] = mCursor.getString(0);			

						i++;
					}
					
		        } 
				finally 
				{
					mCursor.close();
		        }
	    } 
		finally 
		{
	        mDatabase.close();
	    }
		
		gallery.setAdapter(new CelebCamImageAdapter(this));
	}
	Bitmap bitmap;
	public Bitmap getImageAt(int i)
	{

						
						bitmap = CelebCamGlobals.PLACE_HOLDER;
						
						if( mFileNames[i] != null )
						{
							Log.d("PhotoBrowser", mFileNames[i]);
						
							bitmap = BitmapFactory.decodeFile(mFileNames[i]);
							
							int width  = mScreenWidth; 
							int height = mScreenHeight;
							
					       if( bitmap.getWidth() < bitmap.getHeight())
					        {
					        	 width = bitmap.getWidth();
					        	 
					        	 height = (mScreenHeight*bitmap.getWidth())/mScreenWidth;
					        }
					        else if( bitmap.getWidth() > bitmap.getHeight())
					        {
					       	 	height = bitmap.getHeight();
					       	    width = ((mScreenWidth*height)/mScreenHeight);
					        }
							
					       
					       Bitmap bitmap2 = Bitmap.createScaledBitmap( bitmap, width,height, false);
							if( bitmap != null )
				
							bitmap = bitmap2;
						}
						
						return bitmap;
						
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
