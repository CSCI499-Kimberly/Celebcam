package com.celebcam;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CCDebugView extends View{

	private Paint mTextPaint;
	
	private int nextLine;
	private int spaceBetweenLine;
	public CCDebugView( Context context, AttributeSet attributeset )
	{
		super( context, attributeset);
		
		mTextPaint = new Paint();
		
		mTextPaint.setColor( 0xffffffff);
		
		mTextPaint.setTextSize(10);
		
		nextLine = 20;
		spaceBetweenLine = 30;
		
		CCDebug.register(this);
	}

	String totalMemory ;
	String freeMemory ;
	public void onDraw( Canvas canvas )
	{
		if( CCDebug.isOff() )
			return;
		
//		Rect bounds = new Rect();
//		String text;
		//totalMemory  = Long.toString( Runtime.getRuntime().totalMemory()/1024);
		freeMemory   = Long.toString( Runtime.getRuntime().freeMemory()/1024 );
		
		//canvas.drawText("Memory : ", 10, 20, mTextPaint);
		//canvas.drawText(" Total : " + totalMemory + " KB", 10, 45, mTextPaint);
		canvas.drawText(" Free  : " + freeMemory + " KB", 10, 70, mTextPaint);
		
//		canvas.drawText("Line Count : " + LineCount.lineCount, 10, 115, mTextPaint);
//		
//		String publishSize = "Publish Size: " +Integer.toString(CelebCamEffectsLibrary.mPublishSize.width) + " x " + Integer.toString(CelebCamEffectsLibrary.mPublishSize.height);
//		String previewSize = "Preview Size: " +Integer.toString(CelebCamEffectsLibrary.mPreviewSize.width) + " x " + Integer.toString(CelebCamEffectsLibrary.mPreviewSize.height);
//
//		canvas.drawText(publishSize, 10, 145, mTextPaint );
//		canvas.drawText(previewSize, 10, 175, mTextPaint );
//		
//		text = "Data Structures: " + Integer.toString(CCDebug.getMemoryUsage()) + " Bytes ";
//		mTextPaint.getTextBounds(text, 0, text.length(), bounds);
//		canvas.drawRect( bounds, new Paint());
//		
//		canvas.drawText("Data Structures: " + Integer.toString(CCDebug.getMemoryUsage()) + " Bytes ", 10, 220, mTextPaint);
//
//		text = "Data Structures: " + Integer.toString(CCDebug.getMemoryUsage()/(1024*1024)) + " MB ";
//		mTextPaint.getTextBounds(text, 0, text.length(), bounds);
//		bounds.offsetTo(10, 250);
//
//		canvas.drawRect( bounds, new Paint());
//		canvas.drawText(text, 10, 250, mTextPaint);
				
		totalMemory = null;
		freeMemory = null;
		
		invalidate();
	}
}
