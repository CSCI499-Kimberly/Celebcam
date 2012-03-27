package com.celebcam;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
		boolean status = false;
		
		if( currentTile != null )
		{
			status = true;
		}
		else if( currentTile == null )
		{
			currentTile = mFirstTile;
		}
		
		return status;
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
	
	String name;
	
	public Border( String name )
	{
		this.name = name;
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
	
	private ArrayList<Border> mBorder;
	
	private String mSelectedBorder;
	private int mNumberOfTilesHorizontally;
	private int mNumberOfTilesVertically;
	
	public CelebCamBorderView( Context context, AttributeSet attributeSet )
	{
		super( context, attributeSet );
		
		mBorder = new ArrayList<Border>();
<<<<<<< HEAD
//		Tile tile = 
//				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.metal_plate), Border.TOP_TILED,
//				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.green_iguana), Border.BOTTOM_TILED,
//				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.giraffe), Border.LEFT_TILED,
//				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.jeans), Border.RIGHT_TILED, null ) ) ) );
//
//		
//		Border tmp = new Border("Psychedelic");
//		tmp.layers[0].addTile( tile );
//		
//		mBorder.add(tmp);
//		
//		tile = 
//			new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile), Border.TOP_TILED,
//		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile), Border.BOTTOM_TILED,
//		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile),  Border.LEFT_TILED ,
//		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile),  Border.RIGHT_TILED, null ) ) ) );
//
//		Tile tile2 = 
//			new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny), Border.BOTTOM | Border.CENTER, null  );
//
//	tmp = new Border("New York");
//	tmp.layers[0].addTile( tile );
//	tmp.layers[1].addTile( tile2 );
//	
//	mBorder.add(tmp);
//		
//	tmp = new Border("None");
//	mBorder.add(tmp);
	
=======
		Tile tile = 
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.metal_plate), Border.TOP_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.green_iguana), Border.BOTTOM_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.giraffe), Border.LEFT_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.jeans), Border.RIGHT_TILED, null ) ) ) );

		
		Border tmp = new Border("Psychedelic");
		tmp.layers[0].addTile( tile );
		
		mBorder.add(tmp);
		
		tile = 
			new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile), Border.TOP_TILED,
		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile), Border.BOTTOM_TILED,
		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile),  Border.LEFT_TILED ,
		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile),  Border.RIGHT_TILED, null ) ) ) );

		Tile tile2 = 
			new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny), Border.BOTTOM | Border.CENTER, null  );

	tmp = new Border("New York");
	tmp.layers[0].addTile( tile );
	tmp.layers[1].addTile( tile2 );
	
	mBorder.add(tmp);
		
		
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
		CCDebug.registerMemoryWatcher( this );

	}
	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		return false;
	}
	
	public void setPublishSize( Size size )
	{

	}
	
//	public void setBorder( Border border )
//	{
//		mBorder = border;
//	}
//	
//	public Border getBorder()
//	{
//		return mBorder;
//	}
	
	public void selectBorderByName( String name )
	{
		mSelectedBorder = name;
		invalidate();
	}
	
	public void onDraw( Canvas canvas )
	{
		super.onDraw(canvas);		
		
		if( getVisibility() != View.VISIBLE )
			return;
		
		TileLayer currentLayer;
		
		Tile tmpTile;
		
		Iterator<Border> it = mBorder.iterator();
		
		Border border = new Border("NONE");
		
		while( it.hasNext() )
		{
			border = it.next();
			if(border.name.equals(mSelectedBorder))
			{
				break;
			}
		}
		for( int i = 0; i < 3; i++ )
		{
			currentLayer = border.layers[i];
			
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

	
	public void publish( Canvas canvas, Size publishSize, Size previewSize )
	{	
		TileLayer currentLayer;
		
		Tile tmpTile;
		Border border = new Border("NONE");
		
		Iterator<Border> it = mBorder.iterator();
		
		while( it.hasNext() )
		{
			border = it.next();
			if(border.name.equals(mSelectedBorder))
			{
				break;
			}
		}
		
		if( getVisibility() == View.VISIBLE)
		{
			for( int i = 0; i < 3; i++ )
			{
				
				currentLayer = border.layers[i];
				
				while( currentLayer.peek() )
				{
					tmpTile = currentLayer.next();
	
					if( tmpTile.position == Border.LEFT_TILED )
					{
						
						mNumberOfTilesVertically = publishSize.height/tmpTile.bitmap.getHeight();
						
						Log.d("PhotoBrowser", "number vertical : " + Integer.toString(mNumberOfTilesVertically));
						if( (publishSize.height%tmpTile.bitmap.getHeight()) != 0 )
							mNumberOfTilesVertically++;
						
						for( int k = 0; k < mNumberOfTilesVertically; k++ )
						{
							canvas.drawBitmap(tmpTile.bitmap, 0, k*tmpTile.bitmap.getHeight(), null);
						}
					}
					else if( tmpTile.position == Border.RIGHT_TILED )
					{
						
						mNumberOfTilesVertically   = publishSize.height/tmpTile.bitmap.getHeight();
						
						if( (publishSize.height%tmpTile.bitmap.getHeight()) != 0 )
							mNumberOfTilesVertically++;
						
						for( int k = 0; k < mNumberOfTilesVertically; k++ )
						{
							canvas.drawBitmap(tmpTile.bitmap , publishSize.width  - tmpTile.bitmap.getWidth(), k*tmpTile.bitmap.getHeight(), null);
						}
					}
					else if( tmpTile.position == Border.TOP_TILED )
					{
						mNumberOfTilesHorizontally = publishSize.width /tmpTile.bitmap.getWidth();
						
						if( (publishSize.width%tmpTile.bitmap.getWidth()) != 0 )
							mNumberOfTilesHorizontally++;
						
						for( int k = 0; k < mNumberOfTilesHorizontally; k++ )
						{
							canvas.drawBitmap(tmpTile.bitmap, k*tmpTile.bitmap.getWidth(), 0, null);
						}
					}
					else if( tmpTile.position == Border.BOTTOM_TILED )
					{
						mNumberOfTilesHorizontally = publishSize.width /tmpTile.bitmap.getWidth();
						
						if( (publishSize.width %tmpTile.bitmap.getWidth()) != 0 )
							mNumberOfTilesHorizontally++;
						
						for( int k = 0; k < mNumberOfTilesHorizontally; k++ )
						{
							canvas.drawBitmap(tmpTile.bitmap,  k*tmpTile.bitmap.getWidth(), publishSize.height - tmpTile.bitmap.getHeight(), null);
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
							x = publishSize.width - tmpTile.bitmap.getWidth();
						}
	
						if( (tmpTile.position & Border.TOP) > 0 )
						{
							firstPosition = Border.TOP;
							y = 0;
						}
						else if( (tmpTile.position & Border.BOTTOM) > 0 )
						{
							firstPosition = Border.BOTTOM;
							y = publishSize.height - tmpTile.bitmap.getHeight();
						}
						
						if( (tmpTile.position & Border.CENTER) > 0 )
						{
							if( ( firstPosition == Border.LEFT) || ( firstPosition == Border.RIGHT) )
							{
								y = (int)( 0.5*(publishSize.height - tmpTile.bitmap.getHeight()));
							}
							else if( (firstPosition == Border.TOP) || ( firstPosition == Border.BOTTOM))
							{
								x = (int)( 0.5*(publishSize.width - tmpTile.bitmap.getWidth()));
							}
	
						}
						
						canvas.drawBitmap( tmpTile.bitmap, x, y, null);
	
					}
				}
			}
		}	
	}
	
	
}
