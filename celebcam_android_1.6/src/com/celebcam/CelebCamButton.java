package com.celebcam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Button;

class CelebCamColorScheme
{
	 int backgroundColor;
	 int buttonUpColor; 
	 int buttonDownColor;
	 int textColor;
	 
		
		public CelebCamColorScheme( int backgroundColor , int buttonUpColor, int buttonDownColor, int textColor)
		{
			 this.backgroundColor = backgroundColor;
			 this.buttonUpColor = buttonUpColor; 
			 this.buttonDownColor = buttonDownColor;
			 this.textColor = textColor;
		}
}

public class CelebCamButton extends Button{

	private static CelebCamColorScheme mScheme;
	
	public static void setColorScheme( CelebCamColorScheme scheme )
	{
		mScheme = scheme;
	}
	
	private Paint backgroundPaint;
	private Paint foregroundUpPaint;
	private Paint foregroundDownPaint;
	private Paint textPaint;
	
	private RectF backgroundRectF;
	private RectF foregroundRectF;
	
	private float mPadding;
	
	private float[] textPosition;
	
	private boolean mFirstRun;
	
	public CelebCamButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mFirstRun = true;
		
		mPadding = 5;
		
		if( mScheme != null) 
			applyColorScheme();
	}
	
	public void setPadding( float padding )
	{
		mPadding = padding;
	}
	
	public float getPadding()
	{
		return mPadding;
	}

	public void applyColorScheme( )
	{
		if( backgroundPaint == null )
			backgroundPaint = new Paint();
			
		if( foregroundUpPaint == null )
			foregroundUpPaint = new Paint();
			
		if( foregroundDownPaint == null )
			foregroundDownPaint = new Paint();
			
		if( textPaint == null )
			textPaint = getPaint();
			
			backgroundPaint.setColor( mScheme.backgroundColor );
			foregroundUpPaint.setColor(  mScheme.buttonUpColor );
			foregroundDownPaint.setColor(  mScheme.buttonDownColor );
			textPaint.setColor(  mScheme.textColor );

	}
	
	public void setColorScheme( int backgroundColor , int buttonUpColor, int buttonDownColor, int textColor)
	{
		if( backgroundPaint == null )
			backgroundPaint = new Paint();
			
		if( foregroundUpPaint == null )
			foregroundUpPaint = new Paint();
			
		if( foregroundDownPaint == null )
			foregroundDownPaint = new Paint();
			
		if( textPaint == null )
			textPaint = getPaint();
			
			backgroundPaint.setColor( backgroundColor );
			foregroundUpPaint.setColor( buttonUpColor );
			foregroundDownPaint.setColor( buttonDownColor );
			textPaint.setColor( textColor );

	}
	
	public void onDraw( Canvas canvas )
	{
		
		if( mFirstRun )
		{
			backgroundRectF = new RectF(0, 0, getWidth(), getHeight() );
			foregroundRectF = new RectF(mPadding, mPadding, getWidth()-mPadding, getHeight()-mPadding);
		
			Rect r = new Rect();
			textPaint.getTextBounds( getText().toString(), 0, getText().length(), r );
			
			
			textPosition = new float[2];
			
			textPosition[0] = (getWidth() - r.right)/2;
			textPosition[1] = ((getHeight() - r.bottom )/2) + r.bottom;
			 
			mFirstRun = false;
		}
		
		canvas.drawRect( backgroundRectF, backgroundPaint );
		
		if( isPressed() )
			canvas.drawRect( foregroundRectF, foregroundDownPaint );
		else
			canvas.drawRect( foregroundRectF, foregroundUpPaint );

			
			int[] location = new int[2];
			getLocationInWindow( location );
			
			Rect r = new Rect();
			
			getLocalVisibleRect( r );
		canvas.drawText( getText().toString(), textPosition[0], textPosition[1] , textPaint );
	}

}
