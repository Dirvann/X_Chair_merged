package team_310.x_chair;

import android.util.Log;

import com.mygdx.game.MyGdxGame;

import java.util.Objects;

/**
 * Created by Dirk Vanbeveren on 6/04/2017.
 */

class Transcoder {
    static void transcode(String message){
        //This class links all the messages to commands
        //TODO: add commands to the messages

        //for speed
        //mainactivity main = new mainactivity();
        //main.displaySpeed(message);

        Log.i("Receive message", message.substring(0, 4));
        switch(message.substring(0, 4)){
            case commands.VtoC_ACTUAL_SPEED:
                break;
            case commands.VtoC_BATTERY_VOLTAGE:
                Log.i("parse ", message.substring(5, 9));
                int n = Integer.parseInt(message.substring(7, 9), 16);
                MyGdxGame.Battery = String.valueOf(n);

                break;
            case commands.VtoC_RESET:

                break;
            case commands.VtoC_SPEED_SETTING:

                break;
            }
        }

        //mainactivity.speedDisplay.setText(message);
}
