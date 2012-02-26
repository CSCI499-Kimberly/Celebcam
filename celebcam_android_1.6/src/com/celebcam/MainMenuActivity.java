package com.celebcam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.celebcam.R;

import android.view.View;
import android.widget.ImageButton;
import com.celebcam.celebcamapi.*;

public class MainMenuActivity extends Activity {

	private ImageButton startButton;
	private ImageButton browserButton;
	private ImageButton findCelebButton;
	private ImageButton sharingButton;
	private ImageButton settingsButton;
	
	private Context context;
	
	public void onCreate(Bundle bundle)
	{
		super.onCreate( bundle );
		
		setContentView( R.layout.main_menu );
		
		
		startButton = (ImageButton) findViewById( R.id.start_celeb_cam_button );
		
		startButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View view )
			{
				startActivity( new Intent( view.getContext(), DataAcquisitionActivity.class ) );
			}
		});
		
		browserButton = (ImageButton) findViewById( R.id.browse_photos_button );
		
		browserButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View view )
			{
				startActivity( new Intent( view.getContext(), PhotoBrowserActivity.class ) );
			}
		});
		
		findCelebButton = (ImageButton) findViewById( R.id.find_celebrity_button);
		
		findCelebButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View view )
			{
				startActivity( new Intent( view.getContext(), CelebcamApiActivity.class ) );
			}
		});
		
		sharingButton = (ImageButton) findViewById( R.id.sharing_button );
		
		sharingButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View view )
			{
				startActivity( new Intent( view.getContext(),  PersistOptionsActivity.class ) );
			}
		});
		
		settingsButton = (ImageButton) findViewById( R.id.settings_button );
		
		settingsButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View view )
			{
				startActivity( new Intent( view.getContext(), SettingsPrefActivity.class ) );
			}
		});
	}
}
