package com.mygdx.game;

import android.content.Intent;
import android.os.Bundle;


import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	Intent intent;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MyGdxGame(), config);
	}
	/*public void driveMode(View view) {
		Intent intent = new Intent(this, mainactivity.class);

	}*/
	/*public void onConfigurationChanged(Configuration newConfig){ // the newConfig variable contains all the configuration information
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("orientation", "portrait");
			Log.i("ACTIVITY CONTROLLER", "CLOSED");
			finish();                                   //This will close the second screen
		}
	}*/
	public void stop(){
		finish();
	}
}
