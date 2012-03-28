package com.celebcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

class CelebCamDrawerCloseListener implements SlidingDrawer.OnDrawerCloseListener
{
	private CelebCamDrawerGroup mDrawerGroup;
	private int id;
	
	public CelebCamDrawerCloseListener( CelebCamDrawerGroup group, int id )
	{
		mDrawerGroup = group;
		this.id = id;
	}
	
	public void onDrawerClosed() {
	
		mDrawerGroup.close(id);
	}

}

class CelebCamDrawerGroup
{
	SlidingDrawer mMainDrawer;
	CelebCamSlidingDrawer[] mDrawers;
	int mSize;
	int mCount;
	boolean[] mActive;
	int currentOpen;
	int currentClose;
	
	public CelebCamDrawerGroup(int size)
	{
		mCount = 0;
		mSize = size;
		mDrawers = new CelebCamSlidingDrawer[size];
		mActive = new boolean[size];
	}
	
	int mI;
	public CelebCamDrawerGroup(SlidingDrawer mainDrawer, CelebCamSlidingDrawer...drawers)
	{
		mMainDrawer = mainDrawer;
		mSize = drawers.length;
		mDrawers = drawers;
		mActive = new boolean[mSize];
		
		for( int i = 0; i < mSize; i++ )
		{
			mI = i;
			mDrawers[i].setDrawerGroup( this );
			
			mDrawers[i].setOnDrawerCloseListener( new CelebCamDrawerCloseListener(this, i)  );
		}

	}
	
	public void lockAll()
	{
		for(int i = 0; i < mSize; i++ )
			mDrawers[i].lock();
	}
	
	public void open( int index )
	{
		mActive[index] = true;
		
		currentOpen = index;
		
		
		if( mMainDrawer.isOpened())
			mMainDrawer.animateClose();
		
		((CelebCamDrawerHandle) mMainDrawer.getHandle()).enable( false );
		((CelebCamDrawerHandle) mMainDrawer.getHandle()).setOpenState( false );
		
		for( int i = 0; i < mSize; i++ )
		{
			if( i != index )
				close( i );
		}
		
		if( !mDrawers[index].isOpened())
		{
			((CelebCamDrawerHandle) mDrawers[index].getHandle()).enable( true );
			((CelebCamDrawerHandle) mDrawers[index].getHandle()).setOpenState(true);
			mDrawers[index].animateOpen();
		}
		
	}
	
	public void toggle( int index )
	{
		mActive[index] = true;
		
		currentOpen = index;
		
		
		if( mMainDrawer.isOpened())
			mMainDrawer.animateClose();
		
		((CelebCamDrawerHandle) mMainDrawer.getHandle()).enable( false );
		((CelebCamDrawerHandle) mMainDrawer.getHandle()).setOpenState( false );
		
		for( int i = 0; i < mSize; i++ )
		{
			if( i != index )
				close( i );
		}

		((CelebCamDrawerHandle) mDrawers[index].getHandle()).enable( true );
		((CelebCamDrawerHandle) mDrawers[index].getHandle()).setOpenState(true);
		mDrawers[index].animateOpen();

		
	}
	
	public void close( int index )
	{
		if( mDrawers[index].isOpened() || mDrawers[index].isMoving() )
		{
			mDrawers[index].animateClose();
		}
		
		((CelebCamDrawerHandle) mDrawers[index].getHandle()).enable( false );
		mActive[index] = false;
		
		if( isAllClosed() )
		{
			((CelebCamDrawerHandle) mMainDrawer.getHandle()).enable( true );
			((CelebCamDrawerHandle) mMainDrawer.getHandle()).setOpenState( false );
			//mMainDrawer.animateOpen();
		}
	}
	
	public boolean isAllClosed()
	{
		boolean allClosed = true;
		
		for( int i = 0; i < mSize; i++ )
		{
			if( mActive[i] == true )
			{
				allClosed = false;
				break;
			}
		}
		return allClosed;
	}
	
	public boolean interceptRequest( CelebCamSlidingDrawer drawer )
	{
		if( mDrawers[currentOpen] == drawer )
			return true;
			
		return false;
	}
}

public class CelebCamDrawerHandle extends View{

	private boolean mActive;
	private boolean mOpen;
	
	private Bitmap mActiveBitmap;
	private Bitmap mInActiveBitmap;
	
	private SlidingDrawer mParent;
	
	public CelebCamDrawerHandle(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mActiveBitmap = CelebCamGlobals.DOWN_DRAWER_HANDLE;
		
	}

	
	public void setActiveBitmap( Bitmap bitmap )
	{
		mActiveBitmap = bitmap;
	}
	
	public void setInActiveBitmap( Bitmap bitmap )
	{
		mInActiveBitmap = bitmap;
	}
	
	public void setParent( SlidingDrawer parent )
	{
		mParent = parent;
	}
			
	public void enable( boolean active )
	{
		mActive = active;
		
		invalidate();
	}
	
	public void setOpenState( boolean is_open )
	{
		mOpen = is_open;
	}
	
	public boolean getOpenState()
	{
		return mOpen;
	}
	
	public boolean onTouchEvent( MotionEvent event )
	{
		if( mParent.isOpened() )
			return super.onTouchEvent( event );
			
		return false;
	}

	public void onDraw(Canvas canvas)
	{
		super.onDraw( canvas );
		
		if( mActive )
		{
			if( mOpen && mActiveBitmap != null )
				canvas.drawBitmap( mActiveBitmap, 0, 0, null );
			else if( mInActiveBitmap != null )
				canvas.drawBitmap( mInActiveBitmap, 0, 0, null );
		}
	}
}
