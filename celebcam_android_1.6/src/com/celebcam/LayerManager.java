package com.celebcam;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

class ViewList
{
	class ViewNode
	{
		public ViewNode( View view, ViewNode next )
		{
			this.view = view;
			this.next = next;
		}
		
		View view;
		ViewNode next;
	}
	
	private ViewNode mBottom;
	private ViewNode mTop;
	
	public ViewList()
	{
		
	}
	
	public void add( View view)
	{
		if( mBottom == null )
		{
			mBottom = new ViewNode( view, null );
			mTop    = mBottom;
		}
		else
		{
			mTop.next = new ViewNode(view, null );
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
	
	public LayerManager(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext      = context;
		mAttributeSet = attrs;
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
