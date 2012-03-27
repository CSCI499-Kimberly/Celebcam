package com.celebcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GreenScreen extends View {

	private boolean mEnabled;
	private Paint   mPaint;
	private Bitmap  mBitmap;

	public GreenScreen(Context context)
	{
		super(context);
		
<<<<<<< HEAD
=======
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.popeye2);
		
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
		mPaint = new Paint();
		mPaint.setColor(0xff00ff00);
		mPaint.setColor(0xffffffff);
		mEnabled = false;
	}
		
	public GreenScreen(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
<<<<<<< HEAD

=======
		
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.popeye2);
		
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
		mPaint = new Paint();
		mPaint.setColor(0xff00ff00);
		mPaint.setColor(0xffffffff);
		mEnabled = false;
	}
	
	public boolean isOn()
	{
		return mEnabled;
	}
	
	public void setEnable( boolean enabled )
	{
		mEnabled = enabled;
	}
	
	public void toggle()
	{
		mEnabled = !mEnabled;
	}
	
	public void onDraw(Canvas canvas )
	{
		if(isOn())
		{
			canvas.drawPaint( mPaint );
<<<<<<< HEAD
			//canvas.drawBitmap( mBitmap, 0,0,null);
=======
			canvas.drawBitmap( mBitmap, 0,0,null);
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
		}
	}
}
