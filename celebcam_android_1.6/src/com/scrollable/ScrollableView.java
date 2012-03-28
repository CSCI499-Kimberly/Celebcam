package com.scrollable;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;


class Form
{
	static byte RETRACTED = 0;
	static byte EXPANDED  = 1;
	
	private byte mState;
}

class FormList
{
	class FormListNode
	{
	
	}
	
}

public class ScrollableView extends View {

	static byte DOWN  = 1;
	static byte UP    = 2;
	static byte LEFT  = 4;
	static byte RIGHT = 8;
	
	private Context mContext;
	
	private byte mAlphaFade;
	
	private View mHandle;
	
	private byte mDirection;
	
	private boolean mActive;
	
	private RectF mHandleRectF;
	
	private float mAlpha;
	
	private FormList forms;
	
	public ScrollableView( Context context, AttributeSet attributeSet )
	{
		super( context, attributeSet );
		
		mContext = context;
		
		mAlpha = 1;
	}
	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		super.onTouchEvent( motionEvent );
		
		float x = motionEvent.getX();
		float y = motionEvent.getY();
		
		if( motionEvent.getAction() == MotionEvent.ACTION_DOWN )
		{
			if( mHandleRectF.contains( x, y ) )
			{
				mActive = true;
			}
		}
		
		return false;
	}
	
	public void setDirection( byte direction )
	{
		mDirection = direction;
	}
	
	private void move()
	{
		if( (mDirection & DOWN) > 0 )
		{
		}
		else if( (mDirection & UP) > 0 )
		{
		}
		
	    if( (mDirection & LEFT) > 0 )
		{
		}
		else if( (mDirection & RIGHT) > 0 )
		{
		}
	}
	
	public void enableAlphaFade( boolean enable )
	{
		mAlphaFade = enable ? (byte)0 : (byte)1;
		mAlpha     = enable ? 0f : 1f;
	}
	
	private void alphaBlend()
	{
		if(	mAlphaFade == 1 )
		{
			
		}
	}
	
	public void onDraw(Canvas canvas )
	{
		super.onDraw(canvas);
		
		if( mActive )
		{
			alphaBlend();
			move();
			invalidate();
		}
	}
}
