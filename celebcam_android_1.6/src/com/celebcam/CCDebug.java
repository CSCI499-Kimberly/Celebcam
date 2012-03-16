package com.celebcam;

import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

interface CCMemoryWatcher
{
	int getStaticBytes();
	
	int getSizeInBytes();
}

public final class CCDebug {
		
		static private boolean mOn;
		
		static private boolean mFirstRun = true;
		static private int	   mStaticBytes = 0;
		static private List<View> listeners = new ArrayList<View>();
		static private List<Object> memUsers = new ArrayList<Object>();
		
		static boolean isOn()
		{
			return mOn;
		}
		
		static boolean isOff()
		{
			return (!isOn());
		}
		
		static void enable( boolean bSwitch )
		{
			mOn = bSwitch;
		}
		
		static void toggle()
		{
			mOn = !mOn;
			notifyViews();
		}
		
		static void register( View view )
		{
			listeners.add(view);
		}
		
		static void registerMemoryWatcher( Object object )
		{
			memUsers.add( object );
		}
		
		static void unRegister( Object object)
		{
			memUsers.remove( object );
			listeners.remove(object);
		}
		
		static int getMemoryUsage()
		{
			if( mFirstRun )
			{
				mFirstRun = false;
				
				Iterator<Object> it = memUsers.iterator();
				
				while( it.hasNext() )
				{
					mStaticBytes += ((CCMemoryWatcher)it.next()).getStaticBytes();
				}
			}
			
			Iterator<Object> it = memUsers.iterator();
			
			int memoryInBytes = 0;
			
			while( it.hasNext() )
			{
				memoryInBytes += ((CCMemoryWatcher)it.next()).getSizeInBytes();
			}
			
			return memoryInBytes + mStaticBytes;
		}
		
		static void notifyViews()
		{
			Iterator<View> it = listeners.iterator();
			
			while( it.hasNext() )
			{
				((View)it.next()).invalidate();
			}
		}
}
