package com.celebcam;

import android.util.AttributeSet;
import android.util.Log;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.Matrix;

public class CelebCamTextView extends CelebCamOverlaidView implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (1*4)+super.getSizeInBytes();
	}


	public int getStaticBytes() {

		return super.getStaticBytes();
	}
	
	private String mText;
	
	public CelebCamTextView( Context context, AttributeSet attributeSet )
	{
		super(context, attributeSet, INCLUDE_TRANSPARENCY);
		
		CCDebug.registerMemoryWatcher( this );
		
	}

	public void setText( String text )
	{
		mText = text;
		calculateDimensions();
		invalidate();
	}
	
	public String getText()
	{
		return mText;
	}
	
	private void calculateDimensions()
	{
		mCollider[TOP_LEFT_X] = 0;
		mCollider[TOP_LEFT_Y] = 0;
		
		mCollider[TOP_RIGHT_X] = CelebCamFont.getLengthOf(mText);
		mCollider[TOP_RIGHT_Y] = 0;
		
		mCollider[BOTTOM_LEFT_X] = 0;
		mCollider[BOTTOM_LEFT_Y] = CelebCamFont.getHeightOfFont();
		
		mCollider[BOTTOM_RIGHT_X] = CelebCamFont.getLengthOf(mText);
		mCollider[BOTTOM_RIGHT_Y] = CelebCamFont.getHeightOfFont();
		
		mCenter = new float[]{ CelebCamFont.getLengthOf(mText)/2, CelebCamFont.getHeightOfFont()/2 } ;
		
	}

	public int getDrawingX()
	{
		return (int)mCollider[ TOP_LEFT_X ];
	}
	
	public int getDrawingY()
	{
		return (int)mCollider[ TOP_LEFT_Y ];
	}
	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		boolean tmp = super.onTouchEvent(motionEvent);
		
		if( tmp == true && mClicked == true )
		{
			CelebCamController.getCurrent().assignControllerTo(this);
			Log.d("CelebText", "assigning controller now...");
			mClicked = false;
		}
			
		return tmp;
	}
	public void onDraw( Canvas canvas )
	{
		super.onDraw(canvas);
		
		if( mTransformationMatrix == null )
		{
			mTransformationMatrix = new Matrix();
			mTransformationMatrix.setTranslate(getWidth()/2, getHeight()/2 );
			mTransformationMatrix.mapPoints(mCollider);
			mTransformationMatrix.mapPoints(mCenter);
		}
		
		if( mText != null )
			CelebCamFont.paint(canvas, mText,  mTransformationMatrix);
	
		
		if( mControllerLock )
		{
			canvas.drawLines( mCollider, paint );
		}
	}
	
	public void publish( Canvas canvas )
	{
		
		if( mText != null )
		{
			Matrix tmp = new Matrix( mTransformationMatrix );
			tmp.postScale( mPublishSize.width/getWidth(), mPublishSize.height/getHeight());
			CelebCamFont.paint(canvas, mText,  tmp);
		}
		
	}
}
