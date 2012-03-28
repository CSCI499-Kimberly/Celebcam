package com.celebcam;

import android.view.WindowManager;
import android.graphics.*;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CelebCamSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	class CelebCamThread extends Thread
	{
		private SurfaceHolder surfaceHolder;
		private boolean running;
		private Bitmap mBitmap;
		
		public CelebCamThread( SurfaceHolder surfaceHolder, Context context )
		{
			this.surfaceHolder = surfaceHolder;
			mContext = context;
		}
		
		public void setRunning( boolean bool )
		{
			running = bool;
		}
		
		public void run()
		{
			Canvas canvas = null;
			
			while( running )
			{
				updateImage();
				
				try
				{
					
					canvas = surfaceHolder.lockCanvas(null);
					synchronized( surfaceHolder )
					{
						paint( canvas );
						
					}
					
				}
				finally
				{
					if( canvas != null )
					{
						surfaceHolder.unlockCanvasAndPost( canvas );
					}
				}
				
			}
		}
	
		public boolean doKeyDown( int keyCode, KeyEvent keyEvent )
		{

			return false;
		}
		
		public boolean doKeyUp( int keyCode, KeyEvent keyEvent )
		{
			
			return false;
		}
		
		public boolean doTouchEvent( MotionEvent motionEvent )
		{

			
			return true;
		}
		
		Paint p = new Paint();
		public void paint( Canvas canvas )
		{
			if( mBitmap != null )
				canvas.drawBitmap(mBitmap, 0, 0, null);
			
			p.setColor(0xffffffff);
			p.setTextSize( 30 );
			
			canvas.drawText( "SurfaceView", 100, 100, p);
		}

		public void setBitmap(Bitmap bitmap) {
			mBitmap = bitmap;
		}
		
	}
	

	
	static private Context mContext = null;

	static int screenWidth, screenHeight;

    private CelebCamThread thread;
 
    
    
	public CelebCamSurfaceView(Context context, AttributeSet attriSet)
	{
		super( context, attriSet );
		mContext = context;
		
		screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		screenHeight = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		

		SurfaceHolder surfaceholder = getHolder();
		surfaceholder.addCallback(this);
		 
		thread = new CelebCamThread(surfaceholder, context );
		

	}

	static public Context getCurrentContext()
	{
		return mContext;
	}
	
	public CelebCamThread getThread()
	{
		return thread;
	}
	
	public void updateImage()
	{

	}
		
	public void setBitmap( Bitmap bitmap )
	{
		thread.setBitmap( bitmap );
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent)
	{

		thread.doKeyDown( keyCode, keyEvent );
    
    	return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent keyEvent )
	{

     	thread.doKeyUp( keyCode, keyEvent );
     	return true;
	}

	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		thread.doTouchEvent( motionEvent);
		return true;
	}


	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}


	public void surfaceCreated(SurfaceHolder holder) {
		thread.start();
		thread.setRunning( true );
		
	}


	public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        thread.setRunning(false);

        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
		
	}		

}