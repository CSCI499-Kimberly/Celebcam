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
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.graphics.Paint;

public class CelebCamController extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (43*4)+1;
	}


	public int getStaticBytes() {

		return 4;
	}

	final String TAG = "ZoomAndSnapButton";
	
	static CelebCamController current;
	static byte UP   = 0;
	static byte MOVE = 1;
	static byte DOWN = 2;
	
	Rect buttonRect;
	int x; 
	int y;
	
	int pivot_x;
	int pivot_y;

	boolean buttonActive;
	boolean show_up_button;
	
	Paint linePaint = new Paint();
	
	Camera camera;
	
	CelebCamOverlaidView currentOverlaidView;
	CelebCamOverlaidView mDefaultView;
	
	boolean				 mDefault;
	
	int maxZoom;
	int x_offset;
	int y_offset;
	
	byte state;
	
	Bitmap mDefaultBackground; 
	Bitmap mDefaultButtonTop;
	Bitmap mDefaultButtonRotate;
	Bitmap mDefaultButtonRotateTop;

	Bitmap mSecondaryBackground;
	Bitmap mSecondaryButtonTop;
	Bitmap mSecondaryButtonRotate;
	Bitmap mSecondaryButtonRotateTop;
	
	Bitmap mCurrentTop;
	Bitmap mCurrentRotate;
	Bitmap mCurrentRotateTop;
	
	private RectF   mSnapRectF;
	private boolean mSnapActivated;
	
	private float[] mRotateManipulatorRectF;
	
	private Matrix  mRotateManipulatorMatrix;
    private Matrix  mManipulatorHandleMatrix;
    private float[] mCenterOfRotateManipulator;
    private float   mRotateAmount;
    private boolean mRotateActivated;
	private int     mCurrentX;
	private int     mCurrentY;
	private float[] mCenter;
	
	private int mHeight;
	private int mWidth;
	
	private int mKnobHeight;
	private int mKnobWidth;
	
	private float[] mTouchPoints;
	
	Paint paint = new Paint();
	
	public CelebCamController(Context context) {
		super(context);
		
		setup();
	}

	public CelebCamController(Context context, AttributeSet attributes) {
		super(context, attributes);
		
		setup();
	}
	
	private void setup()
	{
		current = this;
		
		mDefaultBackground     = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_background );
		
		mDefaultButtonTop      = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_foreground );
		mDefaultButtonRotate   = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_rotate );
		mDefaultButtonRotateTop= BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_rotate_arrow);
		
		mSecondaryButtonTop       = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_foreground_secondary );
		mSecondaryButtonRotate    = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_rotate_secondary );
		mSecondaryButtonRotateTop = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.button_rotate__arrow_secondary);
		
		mHeight = mDefaultBackground.getHeight();
		mWidth =  mDefaultBackground.getWidth();
		
		mKnobHeight = mDefaultButtonTop.getHeight();
		mKnobWidth  = mDefaultButtonTop.getWidth();
		
		mRotateManipulatorRectF = new float[]{0,0,
											  mDefaultButtonRotateTop.getWidth(),0,
											  0,mDefaultButtonRotateTop.getHeight(),
											  mDefaultButtonRotateTop.getWidth(),mDefaultButtonRotateTop.getHeight()
											  };
		
		mRotateManipulatorMatrix = new Matrix();
    	mManipulatorHandleMatrix = new Matrix(); 
    	
    	mRotateManipulatorMatrix.setTranslate( mWidth/2 - mDefaultButtonRotateTop.getWidth()/2, 0 );
    	mManipulatorHandleMatrix.setTranslate( mWidth/2 - mDefaultButtonRotateTop.getWidth()/2, 0 );
    	mManipulatorHandleMatrix.mapPoints( mRotateManipulatorRectF );
    	
    	mCenterOfRotateManipulator = new float[] { mWidth/2, mHeight/2 };
    	
    	mRotateAmount = mHeight/8;
		
		mSnapRectF = new RectF(0, 0, mKnobWidth, mKnobHeight );
		mSnapRectF.offset((mWidth/2) - mKnobWidth/2 , (mHeight/2) - mKnobHeight/2);
		
		mTouchPoints = new float[4];
		
		paint.setStrokeWidth(5);
		paint.setColor(0xffff3300);
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void assignControllerTo( CelebCamOverlaidView view )
	{
		this.currentOverlaidView.setControllerLock( false );
		
		this.currentOverlaidView = view;
		
		this.currentOverlaidView.setControllerLock( true );
		
		if( currentOverlaidView != mDefaultView )
			mDefault = false;
	}
	
	public void setDefaultControlledView( CelebCamOverlaidView view)
	{
		this.mDefaultView = this.currentOverlaidView = view;
		
		this.currentOverlaidView.setControllerLock( true );
		
		mDefault = true;
	}
	
	public void unlockCurrentView()
	{
		this.currentOverlaidView.setControllerLock( false );
		
		currentOverlaidView = mDefaultView;
		
		this.currentOverlaidView.setControllerLock( true );
		
		mDefault = true;
	}
	
	public boolean isDefault()
	{
		return mDefault;
	}
	
	public void setCamera( Camera camera )
	{
		this.camera = camera;

		//Camera.Parameters parameters = camera.getParameters();
		
		//maxZoom = parameters.getMaxZoom();
	}

	private float leftMostX()
	{
		float minX = mRotateManipulatorRectF[0];
		// 0,2,4,6
		for( int i = 0; i < 4; i++)
		{
			if( minX > mRotateManipulatorRectF[i*2])
				minX = mRotateManipulatorRectF[i*2];
		}
		
		return minX;
	}
	
	private float rightMostX()
	{
		float maxX = mRotateManipulatorRectF[0];
		// 0,2,4,6
		for( int i = 0; i < 4; i++)
		{
			if( maxX < mRotateManipulatorRectF[i*2])
				maxX = mRotateManipulatorRectF[i*2];
		}
		
		
		return maxX;
	}
	
	private float topMostY()
	{
		float minY = mRotateManipulatorRectF[1];
		// 1,3,5,7
		for( int i = 0; i < 4; i++)
		{
			if( minY > mRotateManipulatorRectF[(i*2) + 1])
				minY = mRotateManipulatorRectF[(i*2) + 1];
		}
		
		return minY;
	}
	
	private float bottomMostY()
	{
		float maxY = mRotateManipulatorRectF[1];
		// 1,3,5,7
		for( int i = 0; i < 4; i++)
		{
			if( maxY < mRotateManipulatorRectF[(i*2) + 1])
				maxY = mRotateManipulatorRectF[(i*2) + 1];
		}
		
		return maxY;
	}	
	
	
	private boolean contains( float x, float y )
	{
		
		if( x >= leftMostX() && x <= rightMostX() && y >= topMostY() && y <= bottomMostY() )
			return true;
		
		return false;
	}
	
	public void setCelebView( CelebCamOverlaidView currentOverlaidView )
	{
		this.currentOverlaidView = currentOverlaidView;
	}
	
	int valueForMaxZoom = -1;
	int scaleFactor;

	private float distanceFromInitPoint()
	{
		return (float) Math.sqrt(
				         (Math.abs( mTouchPoints[0] - mTouchPoints[2])* Math.abs( mTouchPoints[0] - mTouchPoints[2]))
				        +(Math.abs( mTouchPoints[1] - mTouchPoints[3])* Math.abs( mTouchPoints[1] - mTouchPoints[3])) 
				        );
	}
	
	public boolean onTouchEvent( MotionEvent motion )
	{

		if( motion.getAction() == MotionEvent.ACTION_DOWN )
		{
			state = DOWN;

			super.onTouchEvent(motion);
			buttonActive=true;
			show_up_button = true;
			
			if( contains( motion.getX(), motion.getY()))
			{
				Log.d(TAG, "rotate activated");
				mRotateActivated = true;
				mTouchPoints[0] = motion.getX();
				mTouchPoints[1] = motion.getY();
			}
			else if( mSnapRectF.contains( motion.getX(), motion.getY()))
			{
				mSnapActivated = true;

				scaleFactor = (int)motion.getX() - (mWidth/2);
			}

		}
		else if( motion.getAction() == MotionEvent.ACTION_MOVE  )
		{

			if( mRotateActivated && motion.getX() >=0 )
			{
				state = MOVE;
				mTouchPoints[2] = motion.getX();
				mTouchPoints[3] = motion.getY();
				
				mRotateAmount = distanceFromInitPoint();
				
				mTouchPoints[0] = motion.getX();
				mTouchPoints[1] = motion.getY();
				
				if( motion.getY() < mCenterOfRotateManipulator[1] )
				{
					if( motion.getX() > mCurrentX )
					{
						currentOverlaidView.rotate( mRotateAmount );
						mRotateManipulatorMatrix.postRotate( mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mManipulatorHandleMatrix.setRotate( mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mCurrentX = (int)motion.getX();
					}
					else if( motion.getX() < mCurrentX )
					{
						currentOverlaidView.rotate( -mRotateAmount );
						mRotateManipulatorMatrix.postRotate( -mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mManipulatorHandleMatrix.setRotate( -mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mCurrentX = (int)motion.getX();
					}
				}
				else if(motion.getY() > mCenterOfRotateManipulator[1] )
				{
					if( motion.getX() > mCurrentX )
					{
						currentOverlaidView.rotate( -mRotateAmount );
						mRotateManipulatorMatrix.postRotate( -mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mManipulatorHandleMatrix.setRotate( -mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mCurrentX = (int)motion.getX();
					}
					else if( motion.getX() < mCurrentX )
					{
						currentOverlaidView.rotate( mRotateAmount );
						mRotateManipulatorMatrix.postRotate( mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mManipulatorHandleMatrix.setRotate( mRotateAmount, mCenterOfRotateManipulator[0], mCenterOfRotateManipulator[1] );
						mCurrentX = (int)motion.getX();
					}
				}
				
				mManipulatorHandleMatrix.mapPoints( mRotateManipulatorRectF );
			}
			else if( mSnapActivated && ( Math.abs( motion.getX() - mWidth/2) > 20 ) )
			{
				
				state = MOVE;
				
				int tmp_x = (int)motion.getX() - (mWidth/2);
				
				Log.d(TAG, "touched at : " + Integer.toString( tmp_x ) );
				
				
				if( tmp_x <= -(mWidth/2) )
					scaleFactor--;
				else if( tmp_x >= (mWidth/2) )
					scaleFactor++;
				else
					scaleFactor = tmp_x;
				
				if( tmp_x <= -(mWidth/2) || tmp_x >= (mWidth/2) )
				{
					currentOverlaidView.zoom(scaleFactor);
					x = tmp_x;
				}
				else if( tmp_x != x )
				{
					currentOverlaidView.zoom(scaleFactor);
					
					x = tmp_x;
				}
				
				
			}
			
//				int tmp_y = (int)motion.getY() - (zoomBitmap.getHeight()/2);
//				
//				if( valueForMaxZoom < y || tmp_y < valueForMaxZoom )
//				{
//					y = tmp_y;
//					
//					
//					//CelebrityOverlayView.current.zoom(1);
//	
//					//if( (y%(maxZoom+1)) >= maxZoom )
//					//	valueForMaxZoom = y;
//				}
				
							
			
		}
			

		if( motion.getAction() == MotionEvent.ACTION_UP )
		{
			
			if( state == DOWN && mSnapActivated )
			{
				super.onTouchEvent(motion);
			}
			
			state = UP;
			
			valueForMaxZoom = 0;
			show_up_button = false;
			
			mRotateActivated = false;
			mSnapActivated   = false;
	
			x = y = 0;
		}
		
		invalidate();
		
		return true;
	}
	
	public void setX( int x )
	{
		this.x = x;
	}
	
	public void setY( int y )
	{
		this.y = y;
	}
	
	public int getX()
	{
			return x;
	}
	
	public int getY()
	{
			return y;
	}
	
	static CelebCamController getCurrent()
	{
		return current;
	}
	
	public void onDraw(Canvas canvas )
	{
			
		super.onDraw(canvas);

		if( !mDefault )
		{
			mCurrentTop    = mDefaultButtonTop;
			mCurrentRotate = mDefaultButtonRotate;
			mCurrentRotateTop = mDefaultButtonRotateTop;
		}
		else
		{
			mCurrentTop    = mSecondaryButtonTop;
			mCurrentRotate = mSecondaryButtonRotate;
			mCurrentRotateTop = mSecondaryButtonRotateTop;
		}
		
		canvas.drawBitmap( mDefaultBackground, 0 , 0, null );

		if(CCDebug.isOn())
			canvas.drawRect(mSnapRectF, paint);
		
		if( x >= 52 )	
			canvas.drawBitmap(mCurrentTop, 52, 0, null );
		else if( x <= -52 )	
			canvas.drawBitmap(mCurrentTop, -52, 0, null );
		else
			canvas.drawBitmap(mCurrentTop, x, 0, null );

		
		canvas.drawBitmap( mCurrentRotate, 0,0, null );
		canvas.drawBitmap( mCurrentRotateTop, mRotateManipulatorMatrix, null );
		
		if( CCDebug.isOn() )
			canvas.drawPoints( mRotateManipulatorRectF, paint);
		
		invalidate();
		
		if( state == MOVE && !mRotateActivated)
		{
			Log.d(TAG, "calling onDraw!");
			if( x <= -52 )
				scaleFactor--;
			else if( x >= 52 )
				scaleFactor++;
			
			if( x <= -52 || x >= 52 )
			{
				currentOverlaidView.zoom(scaleFactor);
			}
			
			invalidate();
		}

	}
	
}
	
