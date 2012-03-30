package com.celebcam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.RectF;
import android.graphics.Paint;

public class TouchSliderGroup extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (5*4);
	}


	public int getStaticBytes() {

		return 4;
	}
	
	private static byte NONE         = -1;
	private static byte SINGLE_CLICK = 0;
	private static byte DOUBLE_CLICK = 1;
	
	private byte mClick;
	
	private byte mClickTimer;
	private byte RESET = 20;
	
	private int mSize;
	
	private float[] mValues;
	private float[] mTouchPoints;
	private RectF[] mButtonRectFs;

	private CelebCamEditView mCelebCamSurfaceView;
	
	private boolean mActive;
	private boolean mFirstRun;
	
	private byte mCurrent;
	
	private Activity mActivity;
	
	public TouchSliderGroup(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
		mFirstRun = true;
		mClick = NONE;
	}
		

	public void setup( int size, CelebCamEditView view, Activity activity)
	{
		
		mSize  = size;

		mCelebCamSurfaceView = view;
		
		mActivity = activity;
		
		mValues  = new float[mSize];
		
		mTouchPoints = new float[2];
		
		mButtonRectFs = new RectF[mSize];
		
		for( int i = 0; i < mSize; i++ )
		{
			mValues[i] = 1;
			mButtonRectFs[i] = new RectF(i*100, 0, 100 +(i*100), 100);
		}
		
		mActive = true;
		
		CCDebug.registerMemoryWatcher( this );
		
		action();
		
		invalidate();
	}
	
	
	public void adjustment()
	{
		for( int i = 0; i < mSize; i++)
		{
			mButtonRectFs[i].left   = i*( getWidth()/mSize);
			mButtonRectFs[i].right  = ((i+1)*( getWidth()/mSize));
			mButtonRectFs[i].top    = 0;
			mButtonRectFs[i].bottom = getHeight();		
			
		}
	}

	public float[] getValues()
	{
		return mValues;
	}
	
	
	public void action()
	{

		new CelebCamEffectsProcessor( mCelebCamSurfaceView, CelebCamEffectsLibrary.ADJUST_COLOR, Channel.getChannel(mCurrent),  mValues[mCurrent] ).execute(  );

	}
	
	public byte getClick()
	{
		return mClick;
	}
	
	public boolean clickTimerActive()
	{
		return ( mClickTimer > 0) ? true : false;
	}
	
	public void updateClickTimer()
	{
		mClickTimer++;
		if( mClickTimer > RESET )
		{
			mClickTimer = 0;
			mClick = NONE;
		}
	}
	
	public void notifyClickTimer()
	{	
		mClickTimer++;
		
		if( mClick == NONE )
		{
			mClick = SINGLE_CLICK;
		}
		else if( mClick == SINGLE_CLICK )
		{
			mClick = DOUBLE_CLICK;
			mClickTimer = 0;
		}
		else if( mClick == DOUBLE_CLICK )
		{
			mClick = SINGLE_CLICK;
		}
		
		invalidate();
	}
	
	public void resetClick()
	{
		mClick = NONE;
	}
	
	public boolean onTouchEvent( MotionEvent motion )
	{
		Log.d("Slider", "onTouchEvent");
		
		if( motion.getAction() == MotionEvent.ACTION_DOWN )
		{
			notifyClickTimer();
							
			for( int i = 0; i < mSize; i++)
			{
				if(mButtonRectFs[i].contains( motion.getX(), motion.getY()))
				{
					mCurrent = (byte)i;
					mTouchPoints[0] = motion.getX();
				    mTouchPoints[1] = motion.getY();
					
					Log.d("Slider", Integer.toString(i) + " touched");
					
					break;
				}
			}

		}
		else if( motion.getAction() == MotionEvent.ACTION_MOVE  )
		{
			float fraction = 1;
			

			fraction = ( Math.abs( motion.getY() - mTouchPoints[1]) ) / ( getHeight() );
			
			if( motion.getY() < mTouchPoints[1] )	           // Slide up
			{
				mValues[ mCurrent ] += fraction;
			}
			else if( motion.getY() > mTouchPoints[1] )       // Slide down
			{

				mValues[ mCurrent ] -= fraction;	
				
				if( mValues[ mCurrent ] < 0 )
					mValues[mCurrent] = 0;
			}

			invalidate();
		}
			

		if( motion.getAction() == MotionEvent.ACTION_UP )
		{

			int rectId = mCurrent;
			
			if( mActive )
			{
				for( int i = 0; i < mSize; i++)
				{
					if(mButtonRectFs[i].contains( motion.getX(), motion.getY()))
					{
						rectId = i;
						mTouchPoints[0] = motion.getX();
					    mTouchPoints[1] = motion.getY();
						
						Log.d("Slider", Integer.toString(i) + " touched");
						
						break;
					}
				}
				
				if( getClick() == DOUBLE_CLICK )
				{
					mValues[ mCurrent ] = 1.0f;
					resetClick();
					invalidate();
				}
				else if( rectId == mCurrent )
				{
					action();
				}
				else
				{
					CelebCamEffectsLibrary.setPreviewBitmap(CelebCamEffectsLibrary.mCCBitmap.toAndroidBitmap());
					
					((CelebCamApplication)mActivity.getApplication()).setBitmap(((FXProcessor)mActivity).mImageSurface.getBitmap());
					mActivity.setResult(Activity.RESULT_OK);
					mActivity.finish();
				}
				
				//mActive = false;
			}
		}
				
		return mActive;
	}
	
	Paint paint = new Paint();
	int[] colors = { 0xffff0000, 0xff00ff00, 0xff0000ff, 0xff000000 };
	RectF mBannerRectF = new RectF();
	
	public void onDraw( Canvas canvas )
	{
		
		if( mFirstRun )
		{
			adjustment();
			
			mBannerRectF.right = getWidth();
			mBannerRectF.bottom = 22;
			
			paint.setTextSize(15);
			mFirstRun = false;
		}
		
		paint.setColor( colors[3]);
		canvas.drawRect( mBannerRectF, paint );
		
		for( int i = 0; i < mSize; i++)
		{
			paint.setColor(colors[i]);
			
			//canvas.drawRect(mButtonRectFs[i], paint );
			String text = Float.toString(mValues[i]);
			if( text.length() > 4)
				text = text.substring(0,4);
			
			canvas.drawText( text + "f", mButtonRectFs[i].left , 20, paint);

		}
		
		progressBar( canvas );
		
		if( clickTimerActive() )
		{
			updateClickTimer();
			invalidate();
		}
	}
	
	RectF mProgressBarRectF = new RectF();
	int[] mProgressBarColors = new int[] { 0x66ffffff, 0xddffffff };
	int mCurrentProgressRect;
	
	public void progressBar( Canvas canvas )
	{
		
		for( int i = 0; i < 5; i++ )
		{
			if( i == mCurrentProgressRect )
				paint.setColor(mProgressBarColors[1]);
			else
				paint.setColor(mProgressBarColors[0]);
			
			canvas.drawRect( mProgressBarRectF, paint );
		}
	}
	
}
