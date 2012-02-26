package com.celebcam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera.Size;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

class Tile implements CCMemoryWatcher {

	public int getSizeInBytes() {

		int m = 0;
		if( bitmap != null )
		{
			m += bitmap.getHeight()*bitmap.getWidth()*4;
		}
		return (3*4) + m;
	}


	public int getStaticBytes() {

		return 0;
	}
	
	Bitmap  bitmap;
	int     position;
	Tile    next;
	
	public Tile( Bitmap bitmap, int position, Tile next )
	{
		this.bitmap   = bitmap;
		this.position = position;
		this.next     = next;
		
		CCDebug.registerMemoryWatcher( this );
	}

}

class TileLayer implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (2*4);
	}


	public int getStaticBytes() {

		return 0;
	}
	
	Tile mFirstTile;
	
	Tile currentTile;
	
	public TileLayer()
	{
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void addTile( Tile tile )
	{
		if( mFirstTile == null )
		{
			mFirstTile = tile;
			currentTile = mFirstTile;
		}
		else
		{
			Tile tmp = mFirstTile;
			
			while( tmp.next != null )
			{
				tmp = tmp.next;
			}
			
			tmp = tile;
		}
	}
	
	public Tile next()
	{
		Tile tmp = currentTile;

		currentTile = currentTile.next;
		
		return tmp;
	}
	
	public boolean peek()
	{
		if( currentTile != null )
			return true;
		
		if( currentTile == null )
		{
			currentTile = mFirstTile;
		}
		
		return false;
	}
}

class Border implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (1*4);
	}


	public int getStaticBytes() {

		return (9*4) + 3;
	}
	
	static final int LEFT   = 16;
	static final int RIGHT  = 4;
	static final int TOP    = 2;
	static final int BOTTOM = 8;
	static final int CENTER = 32;
	
	static final int LEFT_TILED   = 0;
	static final int RIGHT_TILED  = 1;
	static final int TOP_TILED    = 2;
	static final int BOTTOM_TILED = 3;
	
	static final byte BACKGROUND = 0;
	static final byte GROUND     = 1;
	static final byte FOREGROUND = 2;
	
	TileLayer[] layers;
	
	public Border()
	{
		layers = new TileLayer[3];
		
		layers[0] = new TileLayer();
		layers[1] = new TileLayer();
		layers[2] = new TileLayer();
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void addTile( Tile tile, int layer )
	{
		layers[layer].addTile(tile);
	}
}

public class CelebCamBorderView extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (4*4);
	}


	public int getStaticBytes() {

		return 0;
	}
	
	private Border mBorder;
	
	
	private int mNumberOfTilesHorizontally;
	private int mNumberOfTilesVertically;
	

	private Size mPublishSize;
	
	public CelebCamBorderView( Context context, AttributeSet attributeSet )
	{
		super( context, attributeSet );
		
		Tile tile = 
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.metal_plate), Border.TOP_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.green_iguana), Border.BOTTOM | Border.CENTER,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.giraffe), Border.LEFT_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.jeans), Border.RIGHT_TILED, null ) ) ) );

		
		mBorder = new Border();
		
		mBorder.layers[0].addTile( tile );
		
		CCDebug.registerMemoryWatcher( this );

	}
	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		return false;
	}
	
	public void setPublishSize( Size size )
	{
		mPublishSize = size;
	}
	
	public void setBorder( Border border )
	{
		mBorder = border;
	}
	
	public Border getBorder()
	{
		return mBorder;
	}
	
	public void onDraw( Canvas canvas )
	{
		super.onDraw(canvas);		
		
		TileLayer currentLayer;
		
		Tile tmpTile;
		
		for( int i = 0; i < 3; i++ )
		{
			currentLayer = mBorder.layers[i];
			
			while( currentLayer.peek() )
			{
				tmpTile = currentLayer.next();

				if( tmpTile.position == Border.LEFT_TILED )
				{
					
					mNumberOfTilesVertically   = getHeight()/tmpTile.bitmap.getHeight();
					
					if( (getHeight()%tmpTile.bitmap.getHeight()) != 0 )
						mNumberOfTilesVertically++;
					
					for( int k = 0; k < mNumberOfTilesVertically; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap, 0, k*tmpTile.bitmap.getHeight(), null);
					}
				}
				else if( tmpTile.position == Border.RIGHT_TILED )
				{
					
					mNumberOfTilesVertically   = getHeight()/tmpTile.bitmap.getHeight();
					
					if( (getHeight()%tmpTile.bitmap.getHeight()) != 0 )
						mNumberOfTilesVertically++;
					
					for( int k = 0; k < mNumberOfTilesVertically; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap , super.getWidth() - tmpTile.bitmap.getWidth(), k*tmpTile.bitmap.getHeight(), null);
					}
				}
				else if( tmpTile.position == Border.TOP_TILED )
				{
					mNumberOfTilesHorizontally = getWidth()/tmpTile.bitmap.getWidth();
					
					if( (getWidth()%tmpTile.bitmap.getWidth()) != 0 )
						mNumberOfTilesHorizontally++;
					
					for( int k = 0; k < mNumberOfTilesHorizontally; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap, k*tmpTile.bitmap.getWidth(), 0, null);
					}
				}
				else if( tmpTile.position == Border.BOTTOM_TILED )
				{
					mNumberOfTilesHorizontally = getWidth()/tmpTile.bitmap.getWidth();
					
					if( (getWidth()%tmpTile.bitmap.getWidth()) != 0 )
						mNumberOfTilesHorizontally++;
					
					for( int k = 0; k < mNumberOfTilesHorizontally; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap,  k*tmpTile.bitmap.getWidth(), super.getHeight() - tmpTile.bitmap.getHeight(), null);
					}
				}
				else
				{
					Log.d("DataAcquisitionActivity","dual settings");
					int x = 0;
					int y = 0;
					
					int firstPosition = -1;
					
					if( (tmpTile.position & Border.LEFT) > 0 )
					{
						firstPosition = Border.LEFT;
						x = 0;
					}
					else if( (tmpTile.position & Border.RIGHT) > 0 )
					{
						firstPosition = Border.RIGHT;
						x = super.getWidth() - tmpTile.bitmap.getWidth();
					}

					
					if( (tmpTile.position & Border.TOP) > 0 )
					{
						firstPosition = Border.TOP;
						y = 0;
					}
					else if( (tmpTile.position & Border.BOTTOM) > 0 )
					{
						firstPosition = Border.BOTTOM;
						y = super.getHeight() - tmpTile.bitmap.getHeight();
					}
					
					if( (tmpTile.position & Border.CENTER) > 0 )
					{
						if( ( firstPosition == Border.LEFT) || ( firstPosition == Border.RIGHT) )
						{
							y = (int)( 0.5*(super.getHeight() - tmpTile.bitmap.getHeight()));
						}
						else if( (firstPosition == Border.TOP) || ( firstPosition == Border.BOTTOM))
						{
							x = (int)( 0.5*(super.getWidth() - tmpTile.bitmap.getWidth()));
						}

					}
					
					canvas.drawBitmap( tmpTile.bitmap, x, y, null);

				}
			}
		}	
		
		

	}

	
	public void publish( Canvas canvas )
	{
//		if( getVisibility() != VISIBLE )
//			return;
//		
//		super.onDraw(canvas);
//		
//		mNumberOfTilesHorizontally = mPublishSize.width/mWidthOfTile;
//		
//		if( (mPublishSize.width%mWidthOfTile) != 0 )
//			mNumberOfTilesHorizontally++;
//		
//		mNumberOfTilesVertically   = mPublishSize.height/mHeightOfTile;
//		
//		if( (mPublishSize.height%mWidthOfTile) != 0 )
//			mNumberOfTilesVertically++;
//		
//
//		for( int i = 0; i < mNumberOfTilesHorizontally; i++ )
//		{
//			canvas.drawBitmap(mBitmap, i*mWidthOfTile , 0, null);
//			canvas.drawBitmap(mBitmap, i*mWidthOfTile , mPublishSize.height - mHeightOfTile, null);
//		}
//		
//		for( int i = 0; i < mNumberOfTilesVertically; i++ )
//		{
//			canvas.drawBitmap(mBitmap, 0, i*mHeightOfTile, null);
//			canvas.drawBitmap(mBitmap, mPublishSize.width - mWidthOfTile, i*mHeightOfTile, null);
//		}
	}
	
	
}
