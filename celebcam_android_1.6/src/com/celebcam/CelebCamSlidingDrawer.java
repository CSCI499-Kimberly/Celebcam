package com.celebcam;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SlidingDrawer;

public class CelebCamSlidingDrawer extends SlidingDrawer{

	private CelebCamDrawerGroup mDrawerGroup;
	
	public CelebCamSlidingDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setDrawerGroup( CelebCamDrawerGroup drawerGroup )
	{
		mDrawerGroup = drawerGroup;
	}
	
	public CelebCamDrawerGroup getDrawerGroup(  )
	{
		return mDrawerGroup;
	}
	
	public boolean onTouchEvent( MotionEvent event )
	{
		if( !isOpened() )
			return false;
			
		return super.onTouchEvent(event);
	}
	
	public boolean onInterceptTouchEvent (MotionEvent event)
	{
		if( mDrawerGroup.interceptRequest( this ) )
			return super.onInterceptTouchEvent ( event );
		
		return true;

	}
	
}
