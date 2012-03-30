package com.celebcam;

import android.view.View;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;
import android.hardware.Camera.Size;
import android.graphics.Matrix;
import android.graphics.BitmapFactory;

class Circle implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (3*4);
	}


	public int getStaticBytes() {

		return 0;
	}
	
	float x;
	float y;
	float r;
	
	public Circle()
	{
		x = 0;
		y = 0;
		r = 0;
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public Circle( float x, float y, float radius )
	{
		this.x = x;
		this.y = y;
		this.r = radius;
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void translate( float dx, float dy )
	{
		x += dx;
		y += dy;
	}
	
	public void scale( float factor )
	{
		r = r*factor;
	}
	
	private float distanceFromCenter( float x, float y)
	{
		return (float) Math.sqrt(
				         (Math.abs( this.x - x)* Math.abs( this.x - x))
				        +(Math.abs( this.y - y)* Math.abs( this.y - y)) 
				        );
	}
	
	public boolean isWithInArea( float x, float y )
	{
		if( distanceFromCenter( x, y ) <= this.r )
			return true;
		
		return false;
	}
}

class SparkleModel implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (6*4)+1;
	}


	public int getStaticBytes() {

		return 6;
	}
	
	static byte IN_MOTION     = 1;
	static byte NOT_IN_MOTION = 2;
	
	private static byte NORTH = 2;
	private static byte SOUTH = 4;
	private static byte EAST  = 8;
	private static byte WEST  = 16;
	
	float[] mPosition;
	
	float   mSpeed;
	
	final float   INIT_SPEED = 5;
	
	int bitmapIndex;
	
	float scaleFactor;
	
	float angleOfRotation;
	
	byte state;
	
	SparkleModel() {}
	
	SparkleModel( int x, int y, int bitmapIndex, float scaleFactor ) 
	{
		mPosition 		 = new float[2];
		mPosition[0]	 = x;
		mPosition[1]	 = y;
		this.bitmapIndex = bitmapIndex;
		this.scaleFactor = scaleFactor;
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void adjustSpeed(float factor)
	{
		mSpeed *= factor;
		
		if( mSpeed > INIT_SPEED )
		{
			state = IN_MOTION;
		}
		else if( mSpeed <= INIT_SPEED )
		{
			mSpeed = INIT_SPEED;
			state  = NOT_IN_MOTION;
		}
	}
	

	public void moveInDirection( byte direction )
	{
		if( state == NOT_IN_MOTION )
			return;
		
		if( (direction & NORTH) > 0 )
			mPosition[1] -= mSpeed;
		else if( (direction & SOUTH) > 0 )
			mPosition[1] += mSpeed;
		
		if(( direction & EAST ) > 0)
			mPosition[0] += mSpeed;
		if(( direction & WEST) > 0 )
			mPosition[0] -= mSpeed;
	}
	
}

class SparklesEmitter implements CCMemoryWatcher {

	public int getSizeInBytes() {

		int m = 0;
		
		if( mSparkle != null )
			m = mSparkle.getHeight()*mSparkle.getWidth()*4;
		
		return (21*4);
	}


	public int getStaticBytes() {

		return 6;
	}
	
	class Node
	{
		SparkleModel model;
		
		Node next;
		
		public Node() {}
		
		public Node( SparkleModel model )
		{
			this.model = model;
		}
	}
	
	static final byte NORTH = 2;
	static final byte SOUTH = 4;
	static final byte EAST  = 8;
	static final byte WEST  = 16;
	
	private static final byte ACCELERATE = 0;
	private static final byte DECELERATE = 1;
	
	private Node     mFirstNode;
	private Random   mRandom;
	
	private Bitmap   mSparkle;
	private CelebCamBitmap[] mBitmaps;
	private int      mNumberOfBitmaps;
	
	private int    mAmountOfSparkles;
	private int    mBaseColor;
	private float  mColorVariance;
	
	private int  mViewWidth;
	private int  mViewHeight;
	
	Circle  mCircle;
	private Context mContext;
	
	private byte mCircleDirection;
	
	private float mStampedX;
	private float mStampedY;
	
	private boolean mActive;
	
	private int mDisplacement = 10;
	
	private float mDecelerationRate;
	private float mAccelerationRate;
	
	private float mSpeed;
	
	private byte mState;
	
	public SparklesEmitter()
	{
		CCDebug.registerMemoryWatcher( this );
	}
	
	public SparklesEmitter(Context context, int amountOfSparkles, int baseColor, float colorVariance, int width, int height )
	{
		this.mRandom 		   = new Random();
		this.mAmountOfSparkles = amountOfSparkles;
		this.mBaseColor        = baseColor;
		this.mColorVariance    = colorVariance;
		this.mContext 		   = context;
		this.mViewWidth		   = width;
		this.mViewHeight	   = height;
		this.mCircle		   = new Circle();
		
		if( this.mViewHeight <= 0 )
			this.mViewHeight = 1;
		
		if( this.mViewWidth <= 0 )
			this.mViewWidth = 1;
		
		this.mNumberOfBitmaps  = 5;
		this.mSparkle		   = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.sparkle );
		
		this.mCircleDirection = 0;
		
		createBitmaps();
		createAndRandomize();
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void createBitmaps()
	{
		mBitmaps = new CelebCamBitmap[ mNumberOfBitmaps ];
		
		int color = mBaseColor;
		
		for( int i = 0; i < mNumberOfBitmaps; i++ )
		{
			mBitmaps[i] = new CelebCamBitmap(mSparkle);
			
			color *= mColorVariance;
			
			mBitmaps[i].permanentColorOverlay(color);
		}
		//mSparkle = null;
	}
	
	
	private void createAndRandomize()
	{	
		int x = 0, y = 0, bitmapIndex = 0;
		float scaleFactor = 0;
		
		mFirstNode = new Node( new SparkleModel( x, y, bitmapIndex, scaleFactor ) );
		Node tmp = mFirstNode;
		
		for( int i = 0; i < mAmountOfSparkles - 1; i++ )
		{
			x = Math.abs(mRandom.nextInt()) % mViewWidth;
			y = Math.abs(mRandom.nextInt()) % mViewHeight;
			
			bitmapIndex = Math.abs(mRandom.nextInt() % mNumberOfBitmaps);
			
			scaleFactor = (float) ((Math.abs((mRandom.nextInt())%100)+1) / 100.0);
			
			tmp.next = new Node( new SparkleModel( x, y, bitmapIndex, scaleFactor ) );
			
			tmp = tmp.next;
		}
	}
	
	public float getDecelerationRate()
	{
		return mDecelerationRate;
	}
	
	public void setDecelerationRate( float rate )
	{
		this.mDecelerationRate = rate;
	}
	
	public float getAccelerationRate()
	{
		return mAccelerationRate;
	}
	
	public void setAccelerationRate( float rate )
	{
		this.mAccelerationRate = rate;
	}
	
	public void increaseSpeed()
	{
		mSpeed++;
		mDisplacement++;
	}
	
	public void setCircle( Circle circle )
	{
		mCircle = circle;
	}
	
	public void push( byte direction, float speed )
	{
		
	}
	
	public void initialize( float x, float y, float radius )
	{
		this.mCircle.x = x;
		this.mCircle.y = y;
		this.mCircle.r = radius;
	}
	
	public void motion( float x, float y )
	{
		stamp( mCircle );
		
		mCircle.x = x;
		mCircle.y = y;
		
		direction();
		speed();
	}
	
	public void startUpdate()
	{
		mActive = true;
		
	}
	
	public void decelerate()
	{
		mState = DECELERATE;
	}
	
	public void accelerate()
	{
		mState = ACCELERATE;
	}
	
	public void update()
	{
		Node tmp = mFirstNode;
		
		mActive = false;
		
		while( tmp != null )
		{
			if(mCircle.isWithInArea( tmp.model.mPosition[0], tmp.model.mPosition[1] ) )
			{
				if( mState == ACCELERATE )
					tmp.model.adjustSpeed(getAccelerationRate());
				else if( mState == DECELERATE )
					tmp.model.adjustSpeed( getDecelerationRate() );
				
				tmp.model.moveInDirection( mCircleDirection );
				
				if( tmp.model.state == SparkleModel.IN_MOTION )
					mActive = true;
			}
			
			tmp = tmp.next;
		}
		
		
	}
	
	public boolean isActive()
	{ 
		return mActive;
	}
	
	public void stamp( Circle circle )
	{
		mStampedX = circle.x;
		mStampedY = circle.y;
	}

	public byte getDirection()
	{
		return mCircleDirection;
	}
	
	private void direction()
	{
		mCircleDirection = 0;
		
		if( mStampedX < mCircle.x )
		{
			mCircleDirection = (byte) (mCircleDirection | EAST);
		}
		else if( mStampedX > mCircle.x )
		{
			mCircleDirection = (byte) (mCircleDirection | WEST);
		}
		
		if( mStampedY < mCircle.y )
		{
			mCircleDirection = (byte) (mCircleDirection | SOUTH);
		}
		else if( mStampedY > mCircle.y )
		{
			mCircleDirection = (byte) (mCircleDirection | NORTH);
		}
	}
	
	private void speed()
	{
		// speed = |f'(t)| , where f(t) := position at time t
		
		
	}
	
	
	public void draw( Canvas canvas )
	{
		Node tmp = mFirstNode;
		
		Matrix matrix  = new Matrix();
		
		while( tmp != null )
		{
			matrix.setScale(tmp.model.scaleFactor, tmp.model.scaleFactor);
			matrix.postTranslate( tmp.model.mPosition[0] - ((tmp.model.scaleFactor*mSparkle.getWidth())/2)
			                    , tmp.model.mPosition[1] - ((tmp.model.scaleFactor*mSparkle.getHeight())/2) );

			canvas.drawBitmap(mBitmaps[ tmp.model.bitmapIndex ].toAndroidBitmap(), matrix, null);
			
			if( CCDebug.isOn())
			{
				Paint paint = new Paint();
				paint.setColor( 0xffffcc33);
				paint.setStrokeWidth(5);
				
				canvas.drawPoint(tmp.model.mPosition[0], tmp.model.mPosition[1], paint);
			}
			
			tmp = tmp.next;
		}
	}
	
	public void publish( Canvas canvas )
	{
		Node tmp = mFirstNode;
		
		Matrix matrix  = new Matrix();
		
		Ratio ratio = CelebCamEffectsLibrary.publishToPreviewRatio();
		
		while( tmp != null )
		{
			matrix.setScale(tmp.model.scaleFactor*ratio.width, tmp.model.scaleFactor*ratio.height);
			matrix.postTranslate( (tmp.model.mPosition[0] - ((tmp.model.scaleFactor*mSparkle.getWidth() )/2)) *ratio.width
			                    , (tmp.model.mPosition[1] - ((tmp.model.scaleFactor*mSparkle.getHeight())/2)) *ratio.height);

			canvas.drawBitmap(mBitmaps[ tmp.model.bitmapIndex ].toAndroidBitmap(), matrix, null);
			
			tmp = tmp.next;
		}
	}
}

public class CelebCamSparklesView extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (14*4)+1;
	}


	public int getStaticBytes() {

		return 0;
	}
	
	
	private SparklesEmitter mEmitter;

	private Context mContext;
	
	private Size    mPublishSize;
	
	private boolean mActive;
	
	private float mCircleRadius;
	
	private float mCurrentX;
	
	private float mCurrentY;
	
	private float mInitialDownX;
	
	private float mInitialDownY;
	
	private float mSpeedOfBrush;
	
	private float mDistance;
	
	private int   mTime;
	

	public CelebCamSparklesView( Context context, AttributeSet attributeSet )
	{
		super( context, attributeSet );
		
		mContext = context;

		mCircleRadius = 50;
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void setPublishSize( Size publishSize )
	{
		this.mPublishSize = publishSize;
	}
	
	public void enable( boolean isEnabled )
	{
		this.mActive = isEnabled;
	}
	
	public void toggleEnable()
	{
		mActive = !mActive;
	}
	
	private float distance( float x1, float y1, float x2, float y2)
	{
		return (float) Math.sqrt(
				         (( x1 - x2)* ( x1 - x2))
				        +(( y1 - y2)* ( y1 - y2)) 
				       );
	}
	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		if( !mActive )
			return false;
		
		Log.d("Sparkles ", "are active");
		mCurrentX = motionEvent.getX();
		mCurrentY = motionEvent.getY();
		
		mTime++;
		
		if( motionEvent.getAction() == MotionEvent.ACTION_DOWN )
		{
			Log.d( "Sparkles ", "about to initialize");
			mEmitter.initialize( motionEvent.getX(), motionEvent.getY(), mCircleRadius );
			
			Log.d("Pressure is : ", Float.toString(motionEvent.getPressure()));
			
			mInitialDownX = motionEvent.getX();
			mInitialDownY = motionEvent.getY();
		}
		else if( motionEvent.getAction() == MotionEvent.ACTION_MOVE )
		{
			mDistance = distance( mInitialDownX, mInitialDownY, motionEvent.getY(), motionEvent.getY());
			mSpeedOfBrush = mDistance/mTime;
			mEmitter.setAccelerationRate((float)1.5);
			mEmitter.accelerate();
			mEmitter.motion( motionEvent.getX(), motionEvent.getY() );
		}
		else if( motionEvent.getAction() == MotionEvent.ACTION_UP )
		{
			mDistance = distance( mInitialDownX, mInitialDownY, motionEvent.getY(), motionEvent.getY());
			mSpeedOfBrush = mDistance/mTime;
			
			mEmitter.setDecelerationRate( 1/mSpeedOfBrush );
			
			mEmitter.decelerate();
			
			mDistance=0;
			mTime = 0;
		}
	
		mEmitter.startUpdate();
		
		invalidate();
		
		return true;
	}

	
	public void onDraw( Canvas canvas )
	{
		super.onDraw(canvas);
		
		if(getVisibility() != View.VISIBLE)
			return;
		
		if( mEmitter == null )
			mEmitter = new SparklesEmitter(mContext, 100, 0xfff45A82, (float)0.2, getWidth(), getHeight());

		mEmitter.draw(canvas);
		
		if(mEmitter.isActive())
		{
			Paint paint = new Paint();
			paint.setColor(0xffff3300);
			paint.setStrokeWidth(10);
			
			if( CCDebug.isOn())
				canvas.drawCircle(mCurrentX, mCurrentY, mCircleRadius, paint);
			
			mEmitter.update();

			invalidate();
		}
	}
	
	public void publish( Canvas canvas )
	{
		if( getVisibility() == VISIBLE )
		{
			mEmitter.publish(canvas);
		}
	}
}
