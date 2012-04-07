package com.celebcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CelebCamGlobals {

	static  Context sContext;
	
	static Bitmap DOWN_DRAWER_HANDLE; 
	static Bitmap RIGHT_DRAWER_HANDLE;
	static Bitmap RIGHT_DRAWER_HANDLE_BACK;
	static Bitmap PLACE_HOLDER;
	
	static Size MAX_PICTURE_SIZE;
	
	static byte LAUNCH_TWITTER = 0;
	static byte NOT_LAUNCH_TWITTER = 1;
	
	static void setContext( Context context )
	{
		sContext = context;
	}
	
	static void create( Context context )
	{
		sContext = context;
		
		DOWN_DRAWER_HANDLE = BitmapFactory.decodeResource( sContext.getResources(), R.drawable.drawer_handle );

		RIGHT_DRAWER_HANDLE = BitmapFactory.decodeResource( sContext.getResources(), R.drawable.menu);

		RIGHT_DRAWER_HANDLE_BACK = BitmapFactory.decodeResource( sContext.getResources(), R.drawable.menu_back);

		MAX_PICTURE_SIZE = new Size( 100, 100 );
		
		PLACE_HOLDER = Bitmap.createBitmap(1,1, Bitmap.Config.ALPHA_8);
	}
}
