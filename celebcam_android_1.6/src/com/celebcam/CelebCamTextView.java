package com.celebcam;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.util.AttributeSet;
import android.util.Log;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Matrix;

class CelebCamText
{
	String text;
	float  scale;
	float  rotation;
	String fontName;
	
	public CelebCamText()
	{
		
	}
	
	public CelebCamText(String text)
	{
		this.text = text;
	}
	
	public CelebCamText(String text, float scale, String fontName)
	{
		this.text     = text;
		this.scale    = scale;
		this.fontName = fontName;
	}
}

public class CelebCamTextView extends CelebCamOverlaidView implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (1*4)+super.getSizeInBytes();
	}


	public int getStaticBytes() {

		return super.getStaticBytes();
	}
	
	private String mText;
	
	private ArrayList<CelebCamText> mTextList;
	private CelebCamText       current;
	
	public CelebCamTextView( Context context, AttributeSet attributeSet )
	{
		super(context, attributeSet, INCLUDE_TRANSPARENCY);
		
		CCDebug.registerMemoryWatcher( this );
		
		mTextList = new ArrayList<CelebCamText>();
		
	}

	public void addText( CelebCamText text )
	{
		mTextList.add(text);
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
	
	public void scale( float factor)
	{
		super.scale(factor);
		
		CelebCamFont.currentFont.adjustSpacing( factor );
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
	
	public void onDraw2( Canvas canvas )
	{
		super.onDraw(canvas);
		
		ListIterator<CelebCamText> it = (ListIterator<CelebCamText>) mTextList.iterator();
		
		while( it.hasNext())
		{
			CelebCamFont.paint(canvas, it.next());
		}
		
		if( mControllerLock )
		{
			canvas.drawLines( mCollider, paint );
		}
	}
	
	public void publish( Canvas canvas, Size publishSize, Size previewSize )
	{
		
		if(( getVisibility() == View.VISIBLE ) && mText != null )
		{
//			Matrix m = new Matrix(publishSize.width/previewSize.width,0,0,
//								0,publishSize.width/previewSize.width,0,
//								0,0,publishSize.width/previewSize.width);
			
			Matrix m = new Matrix();
			m.setRectToRect(new RectF(0,0,previewSize.width, previewSize.height), new RectF(0,0,publishSize.width, publishSize.height), ScaleToFit.CENTER);
			m.preConcat(mTransformationMatrix);
			
			CelebCamFont.publish(canvas, mText,  m, CelebCamEffectsLibrary.mPublishSize);
		}
		
	}
}
