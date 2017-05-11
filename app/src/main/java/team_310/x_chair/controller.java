package team_310.x_chair;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class controller extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landscape);
    }



    public void onConfigurationChanged(Configuration newConfig){ // the newConfig variable contains all the configuration information
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("orientation", "portrait");
            Log.i("ACTIVITY CONTROLLER", "CLOSED");
            finish();                                   //This will close the second screen
        }
    }
}
