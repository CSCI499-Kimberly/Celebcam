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

public class CelebCamBorderView extends View implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (4*4);
	}


	public int getStaticBytes() {

		return 0;
	}
	
	private ArrayList<CelebCamBorder> mCelebCamBorder;
	
	private String mSelectedCelebCamBorder;
	private int mNumberOfTilesHorizontally;
	private int mNumberOfTilesVertically;
	
	public CelebCamBorderView( Context context, AttributeSet attributeSet )
	{
		super( context, attributeSet );
		
		mCelebCamBorder = new ArrayList<CelebCamBorder>();
		Tile tile = 
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.metal_plate), CelebCamBorder.TOP_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.green_iguana), CelebCamBorder.BOTTOM_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.giraffe), CelebCamBorder.LEFT_TILED,
				new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.jeans), CelebCamBorder.RIGHT_TILED, null ) ) ) );

		
		CelebCamBorder tmp = new CelebCamBorder("Psychadelic");
		tmp.layers[0].addTile( tile );
		
		mCelebCamBorder.add(tmp);
		
		tile = 
			new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile), CelebCamBorder.TOP_TILED,
		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile), CelebCamBorder.BOTTOM_TILED,
		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile),  CelebCamBorder.LEFT_TILED ,
		new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny_tile),  CelebCamBorder.RIGHT_TILED, null ) ) ) );

		Tile tile2 = 
			new Tile(BitmapFactory.decodeResource( context.getResources(), R.drawable.ny), CelebCamBorder.BOTTOM | CelebCamBorder.CENTER, null  );

	tmp = new CelebCamBorder("New York");
	tmp.layers[0].addTile( tile );
	tmp.layers[1].addTile( tile2 );
	
	mCelebCamBorder.add(tmp);
		
	tmp = new CelebCamBorder("None");
	mCelebCamBorder.add(tmp);
	
		CCDebug.registerMemoryWatcher( this );

	}
	
	public boolean onTouchEvent( MotionEvent motionEvent )
	{
		return false;
	}
	
	public void setPublishSize( Size size )
	{

	}
	
//	public void setCelebCamBorder( CelebCamBorder CelebCamBorder )
//	{
//		mCelebCamBorder = CelebCamBorder;
//	}
//	
//	public CelebCamBorder getCelebCamBorder()
//	{
//		return mCelebCamBorder;
//	}
	
	public void selectBorderByName( String name )
	{
		mSelectedCelebCamBorder = name;
		invalidate();
	}
	
	public void onDraw( Canvas canvas )
	{
		super.onDraw(canvas);		
		
		if( getVisibility() != View.VISIBLE )
			return;
		
		int width = CelebCamEffectsLibrary.mPreviewSize.width;
		int height = CelebCamEffectsLibrary.mPreviewSize.height;
		

		TileLayer currentLayer;
		
		Tile tmpTile;
		
		Iterator<CelebCamBorder> it = mCelebCamBorder.iterator();
		
		CelebCamBorder CelebCamBorder = new CelebCamBorder("NONE");
		
		while( it.hasNext() )
		{
			CelebCamBorder = it.next();
			if(CelebCamBorder.name.equals(mSelectedCelebCamBorder))
			{
				break;
			}
		}
		for( int i = 0; i < 3; i++ )
		{
			currentLayer = CelebCamBorder.layers[i];
			
			while( currentLayer.peek() )
			{
				tmpTile = currentLayer.next();

				if( tmpTile.position == CelebCamBorder.LEFT_TILED )
				{
					  
					mNumberOfTilesVertically   = getHeight()/tmpTile.bitmap.getHeight();
					
					if( (getHeight()%tmpTile.bitmap.getHeight()) != 0 )
						mNumberOfTilesVertically++;
					
					for( int k = 0; k < mNumberOfTilesVertically; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap, 0, k*tmpTile.bitmap.getHeight(), null);
					}
				}
				else if( tmpTile.position == CelebCamBorder.RIGHT_TILED )
				{
					
					mNumberOfTilesVertically  = getHeight()/tmpTile.bitmap.getHeight();
					
					if( (getHeight()%tmpTile.bitmap.getHeight()) != 0 )
						mNumberOfTilesVertically++;
					
					for( int k = 0; k < mNumberOfTilesVertically; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap , width - tmpTile.bitmap.getWidth(), k*tmpTile.bitmap.getHeight(), null);
					}
				}
				else if( tmpTile.position == CelebCamBorder.TOP_TILED )
				{
					mNumberOfTilesHorizontally = getWidth()/tmpTile.bitmap.getWidth();
					
					if( (getWidth()%tmpTile.bitmap.getWidth()) != 0 )
						mNumberOfTilesHorizontally++;
					
					for( int k = 0; k < mNumberOfTilesHorizontally; k++ )
					{
						canvas.drawBitmap(tmpTile.bitmap, k*tmpTile.bitmap.getWidth(), 0, null);
					}
				}
				else if( tmpTile.position == CelebCamBorder.BOTTOM_TILED )
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
					
					if( (tmpTile.position & CelebCamBorder.LEFT) > 0 )
					{
						firstPosition = CelebCamBorder.LEFT;
						x = 0;
					}
					else if( (tmpTile.position & CelebCamBorder.RIGHT) > 0 )
					{
						firstPosition = CelebCamBorder.RIGHT;
						x = width - tmpTile.bitmap.getWidth();
					}

					
					if( (tmpTile.position & CelebCamBorder.TOP) > 0 )
					{
						firstPosition = CelebCamBorder.TOP;
						y = 0;
					}
					else if( (tmpTile.position & CelebCamBorder.BOTTOM) > 0 )
					{
						firstPosition = CelebCamBorder.BOTTOM;
						y = super.getHeight() - tmpTile.bitmap.getHeight();
					}
					
					if( (tmpTile.position & CelebCamBorder.CENTER) > 0 )
					{
						if( ( firstPosition == CelebCamBorder.LEFT) || ( firstPosition == CelebCamBorder.RIGHT) )
						{
							y = (int)( 0.5*(super.getHeight() - tmpTile.bitmap.getHeight()));
						}
						else if( (firstPosition == CelebCamBorder.TOP) || ( firstPosition == CelebCamBorder.BOTTOM))
						{
							x = (int)( 0.5*(width - tmpTile.bitmap.getWidth()));
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
		CelebCamBorder CelebCamBorder = new CelebCamBorder("NONE");
		
		Iterator<CelebCamBorder> it = mCelebCamBorder.iterator();
		
		while( it.hasNext() )
		{
			CelebCamBorder = it.next();
			if(CelebCamBorder.name.equals(mSelectedCelebCamBorder))
			{
				break;
			}
		}
		
		if( getVisibility() == View.VISIBLE)
		{
			for( int i = 0; i < 3; i++ )
			{
				
				currentLayer = CelebCamBorder.layers[i];
				
				while( currentLayer.peek() )
				{
					tmpTile = currentLayer.next();
	
					if( tmpTile.position == CelebCamBorder.LEFT_TILED )
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
					else if( tmpTile.position == CelebCamBorder.RIGHT_TILED )
					{
						
						mNumberOfTilesVertically   = publishSize.height/tmpTile.bitmap.getHeight();
						
						if( (publishSize.height%tmpTile.bitmap.getHeight()) != 0 )
							mNumberOfTilesVertically++;
						
						for( int k = 0; k < mNumberOfTilesVertically; k++ )
						{
							canvas.drawBitmap(tmpTile.bitmap , publishSize.width  - tmpTile.bitmap.getWidth(), k*tmpTile.bitmap.getHeight(), null);
						}
					}
					else if( tmpTile.position == CelebCamBorder.TOP_TILED )
					{
						mNumberOfTilesHorizontally = publishSize.width /tmpTile.bitmap.getWidth();
						
						if( (publishSize.width%tmpTile.bitmap.getWidth()) != 0 )
							mNumberOfTilesHorizontally++;
						
						for( int k = 0; k < mNumberOfTilesHorizontally; k++ )
						{
							canvas.drawBitmap(tmpTile.bitmap, k*tmpTile.bitmap.getWidth(), 0, null);
						}
					}
					else if( tmpTile.position == CelebCamBorder.BOTTOM_TILED )
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
						
						if( (tmpTile.position & CelebCamBorder.LEFT) > 0 )
						{
							firstPosition = CelebCamBorder.LEFT;
							x = 0;
						}
						else if( (tmpTile.position & CelebCamBorder.RIGHT) > 0 )
						{
							firstPosition = CelebCamBorder.RIGHT;
							x = publishSize.width - tmpTile.bitmap.getWidth();
						}
	
						if( (tmpTile.position & CelebCamBorder.TOP) > 0 )
						{
							firstPosition = CelebCamBorder.TOP;
							y = 0;
						}
						else if( (tmpTile.position & CelebCamBorder.BOTTOM) > 0 )
						{
							firstPosition = CelebCamBorder.BOTTOM;
							y = publishSize.height - tmpTile.bitmap.getHeight();
						}
						
						if( (tmpTile.position & CelebCamBorder.CENTER) > 0 )
						{
							if( ( firstPosition == CelebCamBorder.LEFT) || ( firstPosition == CelebCamBorder.RIGHT) )
							{
								y = (int)( 0.5*(publishSize.height - tmpTile.bitmap.getHeight()));
							}
							else if( (firstPosition == CelebCamBorder.TOP) || ( firstPosition == CelebCamBorder.BOTTOM))
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
