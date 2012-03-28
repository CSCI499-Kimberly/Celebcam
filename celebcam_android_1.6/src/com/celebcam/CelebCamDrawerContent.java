package com.celebcam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CelebCamDrawerContent extends ScrollView {

	private Paint mBGColor;
	private Paint mTextPaint;
	private String mHeader;
	
	public CelebCamDrawerContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mBGColor = new Paint();
		mBGColor.setColor( 0x99594a2f );
		
		mTextPaint = new Paint();
		mTextPaint.setColor( 0xff000000 );
		
		mHeader = this.toString();
	}

	
	public void setHeader( String header )
	{
		mHeader = header;
	}
	
	public void onDraw( Canvas canvas )
	{
		
		canvas.drawPaint( mBGColor );
		
		canvas.drawText( mHeader, 10, 10, mTextPaint );
		
		super.onDraw( canvas );
	}

}
