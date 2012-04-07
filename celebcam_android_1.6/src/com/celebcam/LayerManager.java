package com.celebcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

class ViewClass
{
	byte mType;
	
}
class ViewList
{
	class ViewNode
	{
		public ViewNode( byte id, View view, ViewNode next )
		{
			this.id   = id;
			this.view = view;
			this.next = next;
		}
		
		byte id;
		View view;
		ViewNode next;
	}
	
	private ViewNode mBottom;
	private ViewNode mTop;
	
	public ViewList()
	{
		
	}

	public void add( byte id, View view)
	{
		if( mBottom == null )
		{
			mBottom = new ViewNode(id, view, null );
			mTop    = mBottom;
		}
		else
		{
			mTop.next = new ViewNode( id, view, null );
			mTop = mTop.next;
		}
	}
	
	public void add( View view)
	{
		if( mBottom == null )
		{
			mBottom = new ViewNode((byte) -1, view, null );
			mTop    = mBottom;
		}
		else
		{
			mTop.next = new ViewNode((byte) -1, view, null );
			mTop = mTop.next;
		}
	}
	
	public void remove( View view )
	{
		
	}
	
	public void shiftPositionBackOne( View view )
	{
		if( mBottom != null && mBottom.view != view )
		{
			int position  = -1;
			ViewNode prev = null;
			ViewNode cur  = mBottom;
			
			while( cur != null )
			{
				
				
				if( view == cur.view )
				{
					break;
				}
				
				position++;
				prev = cur;
				cur = cur.next;
			}
			
			
			prev.next = cur.next;
			cur.next = prev;
			
			ViewNode tmp = mBottom;
			for( int i = 0; i < position-1; i++)
			{
				tmp = tmp.next;
			}
			
			tmp.next = cur;
		}
	}
	
	public void shiftPositionForwardOne( View view )
	{
		
	}
	
	public void onDraw( Canvas canvas )
	{
		ViewNode it = mBottom;
		
		while( it.next != null )
		{
			it.view.draw( canvas );
			
			it = it.next;
		}
	}
}

public class LayerManager extends View {

	private Context      mContext;
	private AttributeSet mAttributeSet;
	private ViewList     mViews;
	
	private byte         mAvailableId;
	
	public LayerManager(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext      = context;
		mAttributeSet = attrs;
		mAvailableId  = -1;
	}
	
	private byte getAvailableId()
	{
		mAvailableId++;
		
		return mAvailableId;
	}
	
	public void addTextViewWithText( String textToAdd )
	{
		CelebCamTextView tmp = new CelebCamTextView( mContext, mAttributeSet);
		tmp.addText( new CelebCamText( textToAdd ));
		
		mViews.add(getAvailableId(), tmp);
	}
	
	public void addTextViewWithText(String nameOfView, String textToAdd )
	{
		
	}
	
	public void addOverlaidViewWithBitmap( String nameOfView, Bitmap bitmapToAdd )
	{
		
	}
	
	public void addSparklesViewWithSparkles( String nameOfView, SparklesEmitter emitter )
	{
		
	}
	
	public void addBorderViewWithBorder( String nameOfView, CelebCamBorder border )
	{
		
	}
	
	public void removeViewByName( String nameOfView )
	{
		
		
	}	
	
	public void addView( View view )
	{
		mViews.add( view );
	}
	
	public void removeView( View view )
	{
		mViews.remove( view );
	}
	

	
	
	public void increaseZOrder( View view )
	{
		mViews.shiftPositionBackOne( view );
	}
	
	public void decreaseZOrder( View view )
	{
		mViews.shiftPositionForwardOne( view );
	}
	
	public void onDraw( Canvas canvas )
	{
		mViews.onDraw(canvas);
	}

}
