package com.celebcam;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class CelebCamEditView extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (1*4);
	}


	public int getStaticBytes() {

		return 0;
	}

	private Bitmap mBitmap;
	private Paint  mBgPaint;

	public CelebCamEditView( Context context )
	{
		super( context );
		
		CCDebug.registerMemoryWatcher( this );
		
		mBgPaint = new Paint();
		mBgPaint.setColor(0xff999999);
	}
	
	public CelebCamEditView( Context context, AttributeSet attributeSet)
	{
		super( context, attributeSet );
		
		CCDebug.registerMemoryWatcher( this );
		
		mBgPaint = new Paint();
		mBgPaint.setColor(0xff999999);
	}
	
	public void setBitmap( Bitmap bitmap )
	{
		mBitmap = bitmap;
		invalidate();
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	public void release()
	{
		if( mBitmap != null )
		{
			mBitmap.recycle();
			mBitmap = null;
		}
		
		invalidate();
	}
	
	public void update()
	{
		invalidate();
	}
	
	public void onDraw( Canvas canvas )
	{
		
		
		if( mBitmap != null )
		{
			canvas.drawPaint(mBgPaint);
			canvas.drawBitmap(mBitmap, (getWidth()-mBitmap.getWidth())/2, (getHeight()-mBitmap.getHeight())/2, null);
		}
		
	}
}
