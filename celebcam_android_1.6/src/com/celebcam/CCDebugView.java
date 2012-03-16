package com.celebcam;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CCDebugView extends View{

	private Paint mTextPaint;
	
	private int nextLine;
	private int spaceBetweenLine;
	public CCDebugView( Context context, AttributeSet attributeset )
	{
		super( context, attributeset);
		
		mTextPaint = new Paint();
		
		mTextPaint.setColor( 0xffff00ee);
		
		mTextPaint.setTextSize(20);
		
		nextLine = 20;
		spaceBetweenLine = 30;
		
		CCDebug.register(this);
	}
	
	public void onDraw( Canvas canvas )
	{
		if( CCDebug.isOff() )
			return;
		
		String totalMemory  = Long.toString( Runtime.getRuntime().totalMemory()/1024);
		String freeMemory   = Long.toString( Runtime.getRuntime().freeMemory()/1024 );
		
		canvas.drawText("Memory : ", 10, 20, mTextPaint);
		canvas.drawText(" Total : " + totalMemory + " KB", 10, 45, mTextPaint);
		canvas.drawText(" Free  : " + freeMemory + " KB", 10, 70, mTextPaint);
		
		canvas.drawText("Line Count : " + LineCount.lineCount, 10, 115, mTextPaint);
		
		String publishSize = "Publish Size: " +Integer.toString(CelebCamEffectsLibrary.mPublishSize.width) + " x " + Integer.toString(CelebCamEffectsLibrary.mPublishSize.height);
		String previewSize = "Preview Size: " +Integer.toString(CelebCamEffectsLibrary.mPreviewSize.width) + " x " + Integer.toString(CelebCamEffectsLibrary.mPreviewSize.height);

		canvas.drawText(publishSize, 10, 145, mTextPaint );
		canvas.drawText(previewSize, 10, 175, mTextPaint );
		
		canvas.drawText("Data Structures: " + Integer.toString(CCDebug.getMemoryUsage()) + " Bytes ", 10, 220, mTextPaint);
		canvas.drawText("Data Structures: " + Integer.toString(CCDebug.getMemoryUsage()/(1024*1024)) + " MB ", 10, 250, mTextPaint);
				
		totalMemory = null;
		freeMemory = null;
		
		invalidate();
	}
}
