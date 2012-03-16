package com.celebcam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Toast;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;



public class CelebCamLibraryView extends View implements CCMemoryWatcher {

	public int getStaticBytes() {
		return 0;
	}

	public int getSizeInBytes() {
		return (4*7);
	}
	
	String TAG = "CelebCamLibraryView";
	CelebCamLibrary mLibrary;
	
	int mX;
	int mY;
	
	RectF mNextRectF;
	RectF mPrevRectF;
	RectF mBrowseRectF;
	RectF mCelebRectF;
	
	boolean mActive;
	boolean mFirstDraw = true;
	
	Bitmap mNextButtonBitmap;
	Bitmap mPrevButtonBitmap;
	Bitmap mBrowseButtonBitmap;
	
	Context mContext;
	
	public CelebCamLibraryView( Context context, AttributeSet attributeSet )
	{
		super( context, attributeSet );
		
		CCDebug.register(this);
		CCDebug.registerMemoryWatcher(this);

		mContext = context;
		
		mNextButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.next_button);
		mPrevButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.prev);
	    mBrowseButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.search);

		mNextRectF = new RectF();
		mPrevRectF = new RectF();
		mBrowseRectF = new RectF();
		mCelebRectF = new RectF();
		
		mLibrary = CelebCamLibrary.getLibrary();
		mActive    = false;
		mFirstDraw = true;
	}

	public boolean onTouchEvent(MotionEvent motionEvent)
	{
		boolean handled = true;
		
		if( motionEvent.getAction() == MotionEvent.ACTION_DOWN )
		{
			float x = motionEvent.getX();
			float y = motionEvent.getY();
			
			
			if( mBrowseRectF.contains( x, y ) )
			{
				Log.d(TAG,"Search pressed!");
				mActive = !mActive;
				CelebCamOverlaidView.current.toggleVisibility();
				
				Toast.makeText(getContext(), "Browse pressed.", 5).show();
			}
			else if( mActive )
			{
				if( mNextRectF.contains( x, y ) )
				{
					Log.d(TAG, "Next pressed!");
					mLibrary.next();

				}
				else if( mPrevRectF.contains( x, y ) )
				{
					Log.d(TAG, "Prev pressed!");
					mLibrary.prev();
					
				}
				else if( mLibrary.doTouchEvent(x,y) )
				{
					CelebCamOverlaidView.current.setBitmap(mLibrary.getImage());
					mActive = !mActive;
					CelebCamOverlaidView.current.toggleVisibility();
				}
			}
			else
			{
				handled = false ;
			}
		}
		
		if( handled )
			invalidate();
		
		return handled;
	}
	
	
	public void onDraw(Canvas canvas)
	{
		
		if( mActive )
			canvas.drawBitmap(mLibrary.getImage(), mX, mY, null );
		
		int x_offset = mX;
		canvas.drawBitmap(mPrevButtonBitmap, x_offset, getHeight()-mPrevButtonBitmap.getHeight(), null );
		
		if( mFirstDraw )
			mPrevRectF = new RectF(x_offset, getHeight() - mPrevButtonBitmap.getHeight(),x_offset+ mPrevButtonBitmap.getWidth() , getHeight() );
		

		x_offset += mPrevButtonBitmap.getWidth();
		
		canvas.drawBitmap(mBrowseButtonBitmap, x_offset, getHeight()-mBrowseButtonBitmap.getHeight(), null );
		
		if( mFirstDraw )
			mBrowseRectF = new RectF(x_offset, getHeight() - mBrowseButtonBitmap.getHeight(),  x_offset+ mBrowseButtonBitmap.getWidth(), getHeight());
		

		x_offset += mBrowseButtonBitmap.getWidth();
		
		canvas.drawBitmap(mNextButtonBitmap, x_offset, getHeight()-mNextButtonBitmap.getHeight(), null );
	
		if( mFirstDraw )
			mNextRectF = new RectF(x_offset, getHeight() - mNextButtonBitmap.getHeight(),  x_offset+ mNextButtonBitmap.getWidth(), getHeight());
		
		
		if( mFirstDraw )
			mFirstDraw = false;
	}

}
