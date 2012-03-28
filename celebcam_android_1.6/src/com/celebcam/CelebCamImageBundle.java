package com.celebcam;

import org.json.JSONObject;

import android.graphics.Bitmap;

public class CelebCamImageBundle {
	private Bitmap     mBitmap;
	private JSONObject mTag;
	
	public CelebCamImageBundle( )
	{

	}
	
	public CelebCamImageBundle( Bitmap bitmap, JSONObject tag)
	{
		mBitmap = bitmap;
		mTag    = tag;
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	public JSONObject getTag()
	{
		return mTag;
	}
}
