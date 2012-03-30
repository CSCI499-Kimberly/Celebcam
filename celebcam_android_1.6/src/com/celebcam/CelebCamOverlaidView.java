package com.celebcam;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera.Size;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class CelebCamOverlaidView extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		int m = 0;
		if( mBitmap != null )
		{
			m += mBitmap.getHeight()*mBitmap.getWidth()*4;
		}
		return (14*4)+2+m;
	}


	public int getStaticBytes() {

		return 18;
	}
		static CelebCamOverlaidView current;
		
		static Context mContext = null;

		static byte TRANSLATION = 0;
		static byte ROTATION    = 1;
		static byte SCALE       = 2;
		
		static byte TOP_LEFT_X  = 0;
		static byte TOP_LEFT_Y  = 1;
		
		static byte TOP_RIGHT_X = 2;
		static byte TOP_RIGHT_Y = 3;
		
		static byte BOTTOM_LEFT_X = 4;
		static byte BOTTOM_LEFT_Y = 5;
		
		static byte BOTTOM_RIGHT_X = 6;
		static byte BOTTOM_RIGHT_Y = 7;
		
		static byte EXCLUDE_TRANSPARENCY = 0;
		static byte INCLUDE_TRANSPARENCY = 1;
		
		static byte UP   = 0;
		static byte MOVE = 1;
		static byte DOWN = 2;
		
		static byte RESTORE = 0;
		static byte RELEASE = 1;
		
		protected boolean mClicked;
		
	    protected Bitmap  mBitmap;
	    
	    protected byte mActiveFlag;
	    
	    protected byte mState;
	    
	    protected byte mMemoryState;
	    
	    protected boolean mActive;
	    protected int     mCurrentX;
	    protected int     mCurrentY;
		protected float[] mCenter;

	    protected Matrix  mTransformationMatrix;
	    
	    protected Matrix  mTranslateMatrix;
	    protected Matrix  mRotateMatrix;
	    protected Matrix  mScaleMatrix;
	    
	    protected byte    mTransformType;
	    
	    protected float[] mCollider;
	    
	    protected RectF  mScaledRectF;
	    
	    protected Paint paint = new Paint();
	    
	    protected Size mPublishSize;
	    
	    protected int mDownCounter;
	    
	    protected boolean mControllerLock;

		public CelebCamOverlaidView(Context context, AttributeSet attriSet)
		{
			super( context, attriSet );
			
			current = this;
			mContext = context;
			
			//mBitmap        
				//Bitmap.createScaledBitmap(
					//	 = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.rihanna )
						//, 100, 100, false)
				//		;

//			mBitmap         = 
//				        Bitmap.createScaledBitmap(
//						mBitmap
//						,300,300, false);
			
			mScaledRectF    = new RectF();
			
			mCollider = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			
			mCenter = new float[]{ 0, 0 } ;

			mActiveFlag = EXCLUDE_TRANSPARENCY;
			
			paint.setStrokeWidth(20);
			paint.setColor(0xffff3300);
			
			CCDebug.register( this );
			
			CCDebug.registerMemoryWatcher( this );

		}

		public CelebCamOverlaidView(Context context, AttributeSet attriSet, byte flag)
		{
			super( context, attriSet );
			
			mContext = context;
			
			mScaledRectF    = new RectF();
			
			mCollider = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			
			mCenter = new float[]{ 0, 0 } ;
			
			mActiveFlag = flag;
			
			paint.setStrokeWidth(20);
			paint.setColor(0xffff3300);
			
			CCDebug.register( this );
		}
		
		public void toggleVisibility()
		{
			if( getVisibility() == View.VISIBLE )
				setVisibility(View.INVISIBLE);
			else
				setVisibility(View.VISIBLE);
		}
		
		public void setBitmap( Bitmap bitmap )
		{
			mBitmap = bitmap;
			
			mScaledRectF    = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			
			mCollider = new float[] { 0, 0,
								 mBitmap.getWidth(), 0,
								 0, mBitmap.getHeight(),
								 mBitmap.getWidth(), mBitmap.getHeight() };
			
			mCenter = new float[]{ mBitmap.getWidth()/2, mBitmap.getHeight()/2 } ;
		}
		
		public Bitmap getBitmap()
		{
			return mBitmap;
		}
		
		public void setPublishSize( Size size )
		{
			mPublishSize = size;
		}
		
		public void scale( float factor)
		{
		
			mTransformationMatrix.postScale(factor, factor );
			Matrix tmp = new Matrix();
			tmp.setScale( factor, factor );
			tmp.mapRect(mScaledRectF);

			tmp.mapPoints(mCollider);
			
			float[] prevCenter = new float[] { mCenter[0], mCenter[1] };
			
			tmp.mapPoints(mCenter);
			
			translate( prevCenter[0] - mCenter[0]   ,  prevCenter[1] - mCenter[1]);
			
			tmp = null;
		}
		
		public void translate( float dx, float dy )
		{
			mTransformationMatrix.postTranslate(dx, dy );
			
			Matrix tmp = new Matrix();
			tmp.setTranslate( dx, dy );
			tmp.mapPoints(mCollider);
			tmp.mapPoints(mCenter);
			
			invalidate();
		}
		
		
		public void rotate( float degrees )
		{
			
			mTransformationMatrix.postRotate(degrees, mCenter[0], mCenter[1] );
			
			Matrix tmp = new Matrix();

			tmp.setRotate( degrees, mCenter[0], mCenter[1]);

			tmp.mapPoints(mCollider);
			
			invalidate();
			
			tmp = null;
		}
		
		
		public void setControllerLock( boolean lock )
		{
			mControllerLock = lock;
			invalidate();
		}
		
		int d;
		
		public void zoom(int distance)
		{
			
			float a = 1;
			if( distance > d )
			{
				a = (float)1.1;
			}
			else if( distance < d )
			{
				a = (float)0.9;
			}
			
			d = distance;
			
			scale( a );
		}
		
		public void stopSmoothZoom(int distance)
		{
			
		}

		protected float leftMostX()
		{
			float minX = mCollider[0];
			// 0,2,4,6
			for( int i = 0; i < 4; i++)
			{
				if( minX > mCollider[i*2])
					minX = mCollider[i*2];
			}
			
			return minX;
		}
		
		protected float rightMostX()
		{
			float maxX = mCollider[0];
			// 0,2,4,6
			for( int i = 0; i < 4; i++)
			{
				if( maxX < mCollider[i*2])
					maxX = mCollider[i*2];
			}
			
			
			return maxX;
		}
		
		protected float topMostY()
		{
			float minY = mCollider[1];
			// 1,3,5,7
			for( int i = 0; i < 4; i++)
			{
				if( minY > mCollider[(i*2) + 1])
					minY = mCollider[(i*2) + 1];
			}
			
			return minY;
		}
		
		protected float bottomMostY()
		{
			float maxY = mCollider[1];
			// 1,3,5,7
			for( int i = 0; i < 4; i++)
			{
				if( maxY < mCollider[(i*2) + 1])
					maxY = mCollider[(i*2) + 1];
			}
			
			return maxY;
		}
		
		private float distance( float x1, float y1, float x2, float y2)
		{
			return (float) Math.sqrt(
					         (( x1 - x2)* ( x1 - x2))
					        +(( y1 - y2)* ( y1 - y2)) 
					       );
		}
		
		protected boolean wasItemTouched( float x, float y )
		{
			
			if( x >= leftMostX() && x <= rightMostX() && y >= topMostY() && y <= bottomMostY() )
			{
				
				if( mActiveFlag == EXCLUDE_TRANSPARENCY )
				{
					float scaleX = (float)mBitmap.getWidth()/distance( mCollider[0], mCollider[1], mCollider[2], mCollider[3]);
					float scaleY = (float)mBitmap.getHeight()/distance( mCollider[0], mCollider[1], mCollider[4], mCollider[5]);
					
					int bmX = (int)(scaleX*( x - mCollider[0]));
					int bmY = (int)(scaleY*( y - mCollider[1]));
					
					if( bmX <= 0 || bmY <= 0 || bmX >= mBitmap.getWidth() || bmY >= mBitmap.getHeight())
						return true;
					
					if( (mBitmap.getPixel(bmX, bmY ) & 0xFF000000) == 0 )
						return false;
				}
				
				return true;
				
			}
			
			return false;
		}
			

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
				if( event.getAction() == MotionEvent.ACTION_DOWN )
				{	
					invalidate();
					
					if( wasItemTouched( event.getX(), event.getY() ) )
					{
						
						mState  = DOWN;
						mActive = true;
						
						
						mCurrentX = (int)event.getX();
						mCurrentY = (int)event.getY();
						
						mTransformType = TRANSLATION;
					
					}
				}
				else if( mActive && event.getAction() == MotionEvent.ACTION_MOVE )
				{
					mState = MOVE;
					
					if( mTransformType == TRANSLATION )
					{
						int xOffset, yOffset;
						
						xOffset = (int)event.getX() - mCurrentX;
				
						yOffset = (int)event.getY() - mCurrentY;
						
						mCurrentX = (int)event.getX();
						mCurrentY = (int)event.getY();
						
						translate(xOffset, yOffset);
					}
				}
				else if( event.getAction() == MotionEvent.ACTION_UP)
				{
					Log.d("Down counter: ", Integer.toString(mDownCounter));
					if( mDownCounter < 10 )
						mClicked = true;
					
					mState = UP;
					mDownCounter = 0;
					mActive = false;
				}
			
			return mActive;
		}


		public void release( CelebCamApplication app)
		{
			if( mBitmap != null )
			{
				app.storeInCache(mBitmap, "OVERLAY");
				mBitmap.recycle();
				mBitmap = null;

				mMemoryState = RELEASE;
				
				invalidate();
			}
		}
		
		public void restore( CelebCamApplication app)
		{
			if( mMemoryState == RELEASE )
			{
				mBitmap = app.loadFromCache("OVERLAY");
				mState = RESTORE;
				
				invalidate();
			}
		}
		
		public void onDraw(Canvas canvas )
		{

			super.onDraw(canvas);
			
			if( mTransformationMatrix == null && mBitmap != null )
			{
				mTransformationMatrix = new Matrix();
				mTransformationMatrix.setTranslate(super.getWidth()/2 - mBitmap.getWidth()/2, super.getHeight() - mBitmap.getHeight());
				mTransformationMatrix.mapPoints(mCollider);
				mTransformationMatrix.mapPoints(mCenter);
			}
			
			if( mBitmap != null )
				canvas.drawBitmap( mBitmap, mTransformationMatrix, null );

			if( CCDebug.isOn() )
			{
				canvas.drawPoints(mCollider, paint );
				canvas.drawPoints(mCenter, paint );
			}
			
			if( mState == DOWN || mState == MOVE)
			{
				mDownCounter++;
				invalidate();
			}
			
		}
			
		public int getX()
		{
			return mCurrentX;
		}
		
		public int getY()
		{
			return mCurrentY;
		}

		public Matrix getMatrix()
		{
			Matrix tmp = new Matrix( mTransformationMatrix );
			return tmp;
			
		}
		
		public Matrix getPublishMatrix()
		{
			Matrix tmp = new Matrix( mTransformationMatrix );
			tmp.postScale(2,2);
			return tmp;
			
		}

	}