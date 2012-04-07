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

public class CelebCamBorder implements CCMemoryWatcher {

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
	
	public CelebCamBorder( String name )
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
