package com.celebcam;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Paint;

class SliderGroup implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (5*4);
	}


	public int getStaticBytes() {

		return 4;
	}
	
	static int count=0; 
	int mSize;
	int mCount;
	CelebCamSlider[] mSliders;
	float[] mValues;
	
	private int id;
	
	public SliderGroup( int size)
	{
		count++;
		id= count;
		
		mSize = size;
		mCount = 0;
		
		mSliders = new CelebCamSlider[mSize];
		mValues  = new float[mSize];
		
		for( int i = 0; i < mSize; i++ )
		{
			mValues[i] = 1;
		}
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public boolean addSlider( CelebCamSlider slider )
	{
		if( mCount < mSize )
		{
			slider.setParentGroup(this);
			slider.setSliderId( mCount );
			
			mSliders[ mCount ] = slider;
			mCount++;
			return true;
		}
		
		return false;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void assignValue( CelebCamSlider slider, float value )
	{
		if( slider.getParentGroup().getId() != id )
			return;
		
		mValues[slider.getSliderId()] = value;
	}
	
	public float[] getValues()
	{
		return mValues;
	}
}

public class CelebCamSlider extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {
		
		int m = 0;
		if( mDefaultBackground != null )
		{
			m += mDefaultBackground.getHeight()*mDefaultBackground.getWidth()*4;
		}

		if( mDefaultButtonTop != null )
		{
			m += mDefaultButtonTop.getHeight()*mDefaultButtonTop.getWidth()*4;
		}
		
		if( mSecondaryButtonTop != null )
		{
			m += mSecondaryButtonTop.getHeight()*mSecondaryButtonTop.getWidth()*4;
		}

		if( mCurrentTop != null )
		{
			m += mCurrentTop.getHeight()*mCurrentTop.getWidth()*4;
		}

		if( mCurrentRotate != null )
		{
			m += mCurrentRotate.getHeight()*mCurrentRotate.getWidth()*4;
		}
		
		return (22*4)+1+m;
	}


	public int getStaticBytes() {

		return 3;
	}

	final String TAG = "Slider";
	
	RectF buttonRect;
	
	byte state;
	
	static byte UP   = 0;
	static byte MOVE = 1;
	static byte DOWN = 2;
	
	Bitmap mDefaultBackground; 
	Bitmap mDefaultButtonTop;

	Bitmap mSecondaryButtonTop;

	Bitmap mCurrentTop;
	Bitmap mCurrentRotate;
	
	private float   mCurrentX;
	private float   mCurrentY;
	private float[] mCenter;
	
	private int mHeight;
	private int mWidth;
	
	private int mKnobHeight;
	private int mKnobWidth;
	
	private float[] mTouchPoints;
	
	private Matrix mManipulatorMatrix;
	
	private boolean mActive;
	
	private int mChannel;
	
	private float mAmount;
	
	private int sign;
	
	private int mSliderId;
	
	private SliderGroup mParentGroup;
	
	private CelebCamEditView mEditView;
	
	public CelebCamSlider(Context context) {
		super(context);
		
		setup();
	}

	public CelebCamSlider(Context context, AttributeSet attributes) {
		super(context, attributes);
		
		setup();
	}
	
	private void setup()
	{
		
		//mDefaultBackground = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.color_level_controls_track );
		//mCurrentTop        = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.color_level_controls_slider );

		mDefaultBackground = mCurrentTop = Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8);
		
		mHeight = mDefaultBackground.getHeight();
		mWidth =  mDefaultBackground.getWidth();

		mKnobHeight = mCurrentTop.getHeight();
		mKnobWidth  = mCurrentTop.getWidth();
		
    	mManipulatorMatrix       = new Matrix(); 	

    	buttonRect = new RectF( 0,( mHeight/2) - 30, mWidth, (mHeight/2 ) + 30 );
		
    	
		mTouchPoints = new float[4];
		
		mCurrentY = (mDefaultBackground.getHeight()/2);
		
		sign = 1;
		mAmount = (float) 1;
		
		CCDebug.registerMemoryWatcher( this );
	}

	public void setChannel( int channel )
	{
		mChannel = channel;
	}
	
	public int getChannel()
	{
		return mChannel;
	}
		
	public int getSliderId()
	{
		return mSliderId;
	}
	
	public void setSliderId( int id )
	{
		this.mSliderId = id;
	}
	
	public void setParentGroup( SliderGroup group )
	{
		this.mParentGroup = group;
	}
	
	public SliderGroup getParentGroup()
	{
		return this.mParentGroup;
	}
	
	public void action()
	{
		mParentGroup.assignValue(this, mAmount);
		mEditView.setBitmap( CelebCamEffectsLibrary.adjustChannels( mParentGroup.getValues()) );
		
		//new CelebCamEffectsProcessor( mEditView, CelebCamEffectsLibrary.ADJUST_COLOR, mChannel, mAmount ).execute(  );
		//mEditView.setBitmap(CelebCamEffectsLibrary.blackAndWhite(mEditView.getBitmap()));
	}
	
	int valueForMaxZoom = -1;
	int scaleFactor;
	
	public boolean onTouchEvent( MotionEvent motion )
	{
		
		if( motion.getAction() == MotionEvent.ACTION_DOWN )
		{
			mActive = false;
			
			if( buttonRect.contains( motion.getX(), motion.getY()))
			{
				mActive = true;
				mTouchPoints[0] = motion.getX();
				mCurrentY = mTouchPoints[1] = motion.getY();
				
				Log.d("Slider ", " is touched");
			}

		}
		else if( mActive && motion.getAction() == MotionEvent.ACTION_MOVE  )
		{
			float fraction = 1;
			
			if( Math.abs( motion.getY() - ( mHeight/2 )) <= ( mHeight/2 ))
				fraction = ( Math.abs( motion.getY() - ( mHeight/2 )) ) / ( mHeight/2 );
			
			if( motion.getY() < mHeight/2 )	           // Upper Half
			{
				mAmount = 1 + fraction;
			}
			else if( motion.getY() > mHeight/2 )       // Lower Half
			{
				mAmount = 1 - fraction;				
			}
			
			if( motion.getY() < mCurrentTop.getHeight()/2 )	
			{
				mCurrentY = mCurrentTop.getHeight()/2 ;
			}
			else if( motion.getY() > mDefaultBackground.getHeight() - mCurrentTop.getHeight()/2) 
			{
				mCurrentY = mDefaultBackground.getHeight() - mCurrentTop.getHeight()/2;
			}
			else
			{
				mManipulatorMatrix.setTranslate(0, (motion.getY() - mCurrentY));
				mManipulatorMatrix.mapRect(buttonRect);
				
				invalidate();
			}

			mCurrentY = motion.getY();
		}
			

		if( motion.getAction() == MotionEvent.ACTION_UP )
		{
			invalidate();

			if( mActive )
			{
				action();
				mEditView.update();
				
				mActive = false;
			}
		}
				
		return mActive;
	}
	
	public void setEditView( CelebCamEditView editView )
	{
		mEditView = editView;
	}
	
	public CelebCamEditView getEditView()
	{
		return mEditView;
	}
	
	Paint paint = new Paint();
	public void onDraw(Canvas canvas )
	{
			paint.setColor(0xffff33cc);
			paint.setTextSize(50);
			
		super.onDraw(canvas);

		canvas.drawBitmap( mDefaultBackground, 0 , 0, null );

		if( CCDebug.isOn())
			canvas.drawRect( buttonRect, paint );

		canvas.drawBitmap(mCurrentTop,0, buttonRect.top, null );
		
		if( CCDebug.isOn() )
			canvas.drawText(Float.toString(mAmount), 20, 400, paint);
	}
	
}
	
